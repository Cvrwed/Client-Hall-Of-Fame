package me.r.touchgrass.module.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.events.EventTick;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;
import me.r.touchgrass.utils.TimeUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;

import java.util.ArrayList;

import static me.r.touchgrass.utils.TimeUtils.getCurrentMS;

/*
 * Created by r on 20/02/2021
 * Updated by kambing on 10/10/2022
 */
@Info(name = "SprintReset", category = Category.Combat, description = "Stops holding the chosen key when sprinting and hitting an enemy thus, comboing them.")
public class SprintReset extends Module {

    private long lastHold = 2000000000L;

    public SprintReset() {
        ArrayList<String> mode = new ArrayList<>();
        mode.add("W Key");
        mode.add("S Key");
        mode.add("D Key");
        addSetting(new Setting("Sword Block", this, true));
        addSetting(new Setting("Type", this, "W Key", mode));
        addSetting(new Setting("Delay", this, 500, 100, 2000, false));
        addSetting(new Setting("Held", this, 100, 50, 250, false));
    }

    protected boolean isTargetValid(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!entity.isEntityAlive()) {
            return false;
        }
        if (!mc.thePlayer.canEntityBeSeen(entity)) {
            return false;
        }
        float range = 4f;
        if (mc.thePlayer.getDistanceToEntity(entity) > range) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
        }
        return true;
    }

    @EventTarget
    public void onUpdate(EventTick e) {
        if (!isEnabled())
            return;
        if (mc.theWorld == null)
            return;
        if (mc.thePlayer == null)
            return;
        if (!mc.thePlayer.isEntityAlive())
            return;
        if (mc.currentScreen != null)
            return;
        Entity ens = null;

        for (Entity e1 : mc.theWorld.getLoadedEntityList()) {
            if (e1 != mc.thePlayer && this.isTargetValid(e1)) {
                if (mc.thePlayer.isSwingInProgress) {
                    ens = e1;
                }
            }
        }

        if (ens != null && mc.thePlayer.isSprinting() && TimeUtils.hasTimePassedMS((long) h2.settingsManager.getSettingByName("Delay").getValue())) {
            if (!(touchgrass.getClient().settingsManager.getSettingByName(this, "Sword Block").isEnabled())) {
                getKey().pressed = getKey() == mc.gameSettings.keyBindBack || getKey() == mc.gameSettings.keyBindRight;
            } else {
                if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                    mc.gameSettings.keyBindUseItem.pressed = true;
                } else getKey().pressed = getKey() == mc.gameSettings.keyBindBack || getKey() == mc.gameSettings.keyBindRight;
            }
            this.lastHold = getCurrentMS();
            TimeUtils.reset();
        }


        if (this.lastHold != -1L && TimeUtils.hasTimePassedMS(this.lastHold, (long) h2.settingsManager.getSettingByName("Held").getValue())) {
            if (!(touchgrass.getClient().settingsManager.getSettingByName(this, "Sword Block").isEnabled())) {
                getKey().pressed = getKey() != mc.gameSettings.keyBindBack && getKey() != mc.gameSettings.keyBindRight;
            } else {
                if (mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                    mc.gameSettings.keyBindUseItem.pressed = false;
                } else getKey().pressed = getKey() != mc.gameSettings.keyBindBack && getKey() != mc.gameSettings.keyBindRight;
            }

            this.lastHold = -1L;
        }
    }

    private KeyBinding getKey() {
        if (touchgrass.getClient().settingsManager.getSettingByName(this, "Type").getMode().equalsIgnoreCase("W Key")) {
            return mc.gameSettings.keyBindForward;
        }
        if (touchgrass.getClient().settingsManager.getSettingByName(this, "Type").getMode().equalsIgnoreCase("S Key")) {
            return mc.gameSettings.keyBindBack;
        }
        if (touchgrass.getClient().settingsManager.getSettingByName(this, "Type").getMode().equalsIgnoreCase("D Key")) {
            return mc.gameSettings.keyBindRight;
        }
        return mc.gameSettings.keyBindForward; // so there will be no null pointer
    }
}
