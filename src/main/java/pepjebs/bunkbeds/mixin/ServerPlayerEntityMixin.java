package pepjebs.bunkbeds.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.bunkbeds.BunkBedsMod;

import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
abstract public class ServerPlayerEntityMixin extends Entity {

    private static ServerPlayerEntityMixin player;

    public ServerPlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "setSpawnPoint",
            at = @At("RETURN")
    )
    private void pushBedSpawn(
            ServerPlayerEntity.Respawn respawn,
            boolean sendMessage,
            CallbackInfo ci) {
        if (respawn == null) {
            return;
        }
        var position = respawn.respawnData().getPos();
        if (position != null && !respawn.forced()
                && respawn.respawnData().getDimension() == World.OVERWORLD) {
            var playerName = this.getName().getString();
            BunkBedsMod.PLAYER_BEDS_KEY.get(
                    this.getEntityWorld()
            ).pushPlayerSpawnPos(
                    this.getEntityWorld(),
                    playerName,
                    position
            );
        }
    }

    @Inject(method = "getRespawnTarget", at = @At("RETURN"))
    private void injectAtRespawnTarget(
            boolean alive,
            TeleportTarget.PostDimensionTransition postDimensionTransition,
            CallbackInfoReturnable<TeleportTarget> ci
    ) {
        player = this;
    }

    @Inject(
            method = "findRespawnPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;empty()Ljava/util/Optional;"
            ),
            cancellable = true
    )
    private static void injectSpawnFromBedStack(
            ServerWorld world, ServerPlayerEntity.Respawn respawn, boolean bl,
            CallbackInfoReturnable<Optional<ServerPlayerEntity.RespawnPos>> cir
    ) {
        var playerName = player.getName().getString();
        BunkBedsMod.PLAYER_BEDS_KEY.get(world).popPlayerSpawnPos(world, playerName);
        var spawns = BunkBedsMod.PLAYER_BEDS_KEY.get(world).playerSpawns(playerName);
        BunkBedsMod.LOGGER.info(spawns);
        if (spawns == null || spawns.isEmpty()){
            cir.setReturnValue(Optional.empty());
        } else {
            var newPos = spawns.get(0);
            var blockState = world.getBlockState(newPos);
            var wakeUp = BedBlock.findWakeUpPosition(
                    EntityType.PLAYER, world, newPos, blockState.get(BedBlock.FACING), respawn.respawnData().pitch());
            if (wakeUp.isEmpty()) {
                return;
            }
            cir.setReturnValue(Optional.of(new ServerPlayerEntity.RespawnPos(
                    wakeUp.get(),
                    respawn.respawnData().yaw(),
                    respawn.respawnData().pitch()
            )));
        }
    }
}
