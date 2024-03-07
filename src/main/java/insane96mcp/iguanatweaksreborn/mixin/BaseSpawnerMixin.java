package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.world.spawners.Spawners;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

	@ModifyExpressionValue(
			method = "serverTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BaseSpawner;isNearPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
	)
	private boolean serverTick(boolean isPlayerNearby, ServerLevel level, BlockPos blockPos) {
		if (isPlayerNearby && Spawners.onSpawnerServerTick((BaseSpawner) (Object) this))
			return false;
		return isPlayerNearby;
	}

	@ModifyExpressionValue(
			method = "clientTick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BaseSpawner;isNearPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
	)
	private boolean clientTick(boolean isPlayerNearby, Level level, BlockPos blockPos) {
		if (isPlayerNearby && Spawners.onSpawnerClientTick((BaseSpawner) (Object) this))
			return false;
		return isPlayerNearby;
	}
}