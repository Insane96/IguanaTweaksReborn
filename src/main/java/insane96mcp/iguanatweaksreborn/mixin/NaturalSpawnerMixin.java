package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.mobs.Spawning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), method = "isRightDistanceToPlayerAndSpawnPoint")
	private static boolean onWorldSpawnCheck(BlockPos instance, Position position, double v, ServerLevel pLevel, ChunkAccess pChunk, BlockPos.MutableBlockPos pPos, double pDistance) {
		if (Spawning.allowWorldSpawnSpawn)
			return false;
		return pLevel.getSharedSpawnPos().closerToCenterThan(new Vec3((double)pPos.getX() + 0.5D, pPos.getY(), (double)pPos.getZ() + 0.5D), 24.0D);
	}
}
