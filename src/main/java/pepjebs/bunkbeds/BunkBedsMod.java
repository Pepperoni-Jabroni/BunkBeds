package pepjebs.bunkbeds;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BunkBedsMod {

    public static final String MOD_ID = "bunk_beds";
    public static final ComponentKey<SpawnStackingComponent> PLAYER_BEDS_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier(MOD_ID, "player_beds"),
                    SpawnStackingComponent.class);
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ThreadLocal<Entity> PLAYER = new ThreadLocal<>();
}
