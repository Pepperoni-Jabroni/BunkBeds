package pepjebs.bunkbeds.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pepjebs.bunkbeds.BunkBedsMod;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(
            method = "findRespawnPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;empty()Ljava/util/Optional;"
            ),
            cancellable = true
    )
    private static void injectSpawnFromBedStack(
            ServerWorld world, BlockPos pos, float angle, boolean forced, boolean alive,
            CallbackInfoReturnable<Optional<Vec3d>> cir
    ) {
        var playerName = BunkBedsMod.PLAYER.get().getName().getString();
        BunkBedsMod.PLAYER_BEDS_KEY.get(world).popPlayerSpawnPos(world, playerName);
        var spawns = BunkBedsMod.PLAYER_BEDS_KEY.get(world).playerSpawns(playerName);
        if (spawns == null || spawns.isEmpty()){
            cir.setReturnValue(Optional.empty());
        } else {
            var newPos = spawns.get(0);
            var blockState = world.getBlockState(newPos);
            var wakeUp = BedBlock.findWakeUpPosition(
                    EntityType.PLAYER, world, newPos, blockState.get(BedBlock.FACING), angle);
            cir.setReturnValue(wakeUp);
        }
    }
}
