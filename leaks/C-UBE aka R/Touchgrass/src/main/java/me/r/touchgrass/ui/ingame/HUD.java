package me.r.touchgrass.ui.ingame;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventUpdate;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;
import me.r.touchgrass.ui.ingame.style.Style;
import me.r.touchgrass.ui.ingame.style.styles.Classic;
import me.r.touchgrass.ui.ingame.style.styles.New;
import me.r.touchgrass.ui.ingame.style.styles.Tephra;

import java.util.ArrayList;

@Info(name = "HUD", description = "The overlay", category = Category.Gui)
public class HUD extends Module {

    public Style style;

    // this module basically only exists to have general settings, also to disable the hud alltogether

    public HUD() {
        ArrayList<String> style = new ArrayList<>();
        style.add("Classic");
        style.add("New");
        style.add("Tephra");
        ArrayList<String> time = new ArrayList<>();
        time.add("24H");
        time.add("12H");

        addSetting(new Setting("Time Format", this, "24H", time));
        addSetting(new Setting("Style", this, "New", style));
        this.style = new New();
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.updateStyles();
    }

    public void updateStyles() {
        switch(h2.settingsManager.getSettingByName(this, "Style").getMode()) {
            case "Classic":

                // background = false
                // color = white
                Classic.sortModules();
                Classic.loadSettings();
                h2.hud.style = new Classic();
                break;
            case "Tephra":

                // background = true
                // color = rainbow

                Classic.sortModules();
                Tephra.loadSettings();
                h2.hud.style = new Tephra();
                break;
            case "New":

                // background = true
                // outline = true
                // color = rainbow

                New.sortModules();
                New.loadSettings();
                h2.hud.style = new New();
                break;
        }
    }
}
