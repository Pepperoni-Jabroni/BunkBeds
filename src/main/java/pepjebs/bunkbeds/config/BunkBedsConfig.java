package pepjebs.bunkbeds.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import pepjebs.bunkbeds.BunkBedsMod;

@Config(name = BunkBedsMod.MOD_ID)
public class BunkBedsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @Comment("The max number of beds to be kept in the respawn stack")
    public int maxBunkBedStackSize = 5;

}
