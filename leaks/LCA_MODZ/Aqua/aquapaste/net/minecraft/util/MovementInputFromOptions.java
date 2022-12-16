// 
// Decompiled by Procyon v0.5.36
// 

package net.minecraft.util;

import net.minecraft.client.Minecraft;
import intent.AquaDev.aqua.Aqua;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;
    
    public MovementInputFromOptions(final GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }
    
    @Override
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0f;
        this.moveForward = 0.0f;
        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }
        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }
        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }
        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }
        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();
        if (this.sneak) {
            final boolean scaffold = Aqua.setmgr.getSetting("ScaffoldLegitPlace").isState() && Aqua.moduleManager.getModuleByName("Scaffold").isToggled() && !Minecraft.getMinecraft().gameSettings.keyBindJump.pressed;
            if (scaffold) {
                if (Aqua.moduleManager.getModuleByName("Disabler").isToggled()) {
                    this.moveStrafe *= (float)0.9;
                    this.moveForward *= (float)0.9;
                }
                else {
                    this.moveStrafe *= (float)Aqua.setmgr.getSetting("ScaffoldSneakModify").getCurrentNumber();
                    this.moveForward *= (float)Aqua.setmgr.getSetting("ScaffoldSneakModify").getCurrentNumber();
                }
            }
            else {
                this.moveStrafe *= (float)0.3;
                this.moveForward *= (float)0.3;
            }
        }
    }
}