package net.optifine.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.Config;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class GuiMessage extends GuiScreen {
   private final GuiScreen parentScreen;
   private final String messageLine1;
   private final String messageLine2;
   private final List listLines2 = Lists.newArrayList();
   protected final String confirmButtonText;
   private int ticksUntilEnable;

   public GuiMessage(GuiScreen parentScreen, String line1, String line2) {
      this.parentScreen = parentScreen;
      this.messageLine1 = line1;
      this.messageLine2 = line2;
      this.confirmButtonText = I18n.format("gui.done");
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
      this.listLines2.clear();
      this.listLines2.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      Config.getMinecraft().displayGuiScreen(this.parentScreen);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for(Object e : this.listLines2) {
         String s = (String)e;
         this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (--this.ticksUntilEnable == 0) {
         for(GuiButton guibutton : this.buttonList) {
            guibutton.enabled = true;
         }
      }
   }
}
