package pepjebs.bunkbeds;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import pepjebs.bunkbeds.config.BunkBedsConfig;

public class BunkBedsMod implements ModInitializer {

    public static final String MOD_ID = "bunk_beds";
    public static final ComponentKey<SpawnStackingComponent> PLAYER_BEDS_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    Identifier.of(MOD_ID, "player_beds"),
                    SpawnStackingComponent.class);
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static BunkBedsConfig CONFIG = null;
    public static int DEFAULT_MAX_BED_STACK = 5;

    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().isModLoaded("cloth-config")) {
            AutoConfig.register(BunkBedsConfig.class, JanksonConfigSerializer::new);
            CONFIG = AutoConfig.getConfigHolder(BunkBedsConfig.class).getConfig();
        }
    }
}
