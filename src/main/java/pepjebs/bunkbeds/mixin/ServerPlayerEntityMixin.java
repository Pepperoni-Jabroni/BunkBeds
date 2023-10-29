package pepjebs.bunkbeds.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.bunkbeds.BunkBedsMod;

@Mixin(ServerPlayerEntity.class)
abstract public class ServerPlayerEntityMixin extends Entity {

    @Shadow
    private RegistryKey<World> spawnPointDimension;

    public ServerPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        BunkBedsMod.PLAYER.set(this);
    }

    @Inject(
            method = "setSpawnPoint",
            at = @At("RETURN")
    )
    private void pushBedSpawn(
            RegistryKey<World> dimension,
            @Nullable BlockPos pos,
            float angle,
            boolean forced,
            boolean sendMessage,
            CallbackInfo ci) {
        if (pos != null && this.spawnPointDimension == World.OVERWORLD && !forced) {
            var playerName = this.getEntityName();
            BunkBedsMod.PLAYER_BEDS_KEY.get(this.getWorld()).pushPlayerSpawnPos(playerName, pos);
        }
    }

    @Inject(
            method = "getSpawnPointPosition",
            at = @At("RETURN")
    )
    public void setPlayerLocal(CallbackInfoReturnable<BlockPos> cir) {
        BunkBedsMod.PLAYER.set(this);
    }
}
