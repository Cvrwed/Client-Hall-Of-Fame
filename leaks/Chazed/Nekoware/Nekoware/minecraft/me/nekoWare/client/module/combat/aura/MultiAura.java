
package me.nekoWare.client.module.combat.aura;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.nekoWare.client.event.events.UpdateEvent;
import me.nekoWare.client.module.combat.Aura;
import me.nekoWare.client.util.combat.CombatUtil;
import me.nekoWare.client.util.combat.RotationUtils;
import me.nekoWare.client.util.misc.Timer;
import me.nekoWare.client.util.packet.PacketUtil;

public class MultiAura {
    private static Timer timer = new Timer();
    public static List<EntityLivingBase> targets = new ArrayList<>();
    private static int index = 0;
    private static Timer t = new Timer();
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();
    private static int targetIndex;
    public static boolean chance(int percent) {
        return RANDOM_NUMBER_GENERATOR.nextInt(100) <= percent;
    }
    public static void doUpdate(final Aura aura, final UpdateEvent e, final Minecraft mc) {
        double range = aura.getDouble("Range");
        final int blockPercentage = aura.getDouble("Block Chance").intValue();
        final boolean teams = false;
        final List<EntityLivingBase> targets = CombatUtil.getTargets(teams, 100, false, true, range, true);
        final boolean autoBlock = aura.getBool("Auto Block");
        final long clickSpeed = aura.getDouble("Click Speed").longValue();
        Aura.isBlocking = chance(blockPercentage) && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                && autoBlock && CombatUtil.canBlock(teams, false, true, range + 4.0, true);
        if (Aura.isBlocking) {
            if (e.isPost()) {
                mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 140);
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            } else {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
            }
        }
        if (!targets.isEmpty()) {
            if (targetIndex >= targets.size()) {
                targetIndex = 0;
            }
            Aura.currentEntity = targets.get(0);
        	if(Aura.Rots == true) {
			float[] rotations = RotationUtils.getRotations((EntityLivingBase) targets.get(targetIndex));
			e.setYaw(rotations[0]);
			e.setPitch(rotations[1]);
			mc.thePlayer.rotationYawHead = rotations[0];
			mc.thePlayer.rotationPitchHead = rotations[1];
			mc.thePlayer.renderYawOffset = rotations[0];
        	}
        }
        else {
            targetIndex = 0;
            Aura.currentEntity = null;
        }
        if (e.isPost()) {
            return;
        }
        if (!timer.delay(1000L / clickSpeed)) {
            return;
        }
        for (final Entity entity : targets) {
            mc.thePlayer.swingItem();
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
        ++targetIndex;
        timer.reset();
    }
}
