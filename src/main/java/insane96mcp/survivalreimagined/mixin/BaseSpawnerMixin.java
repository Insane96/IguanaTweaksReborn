package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.misc.feature.Spawners;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {

	@Inject(at = @At("HEAD"), method = "serverTick", cancellable = true)
	private void serverTick(ServerLevel p_151312_, BlockPos p_151313_, CallbackInfo callback) {
		if (Spawners.onSpawnerServerTick((BaseSpawner) (Object) this))
			callback.cancel();
	}

	@Inject(at = @At("HEAD"), method = "clientTick", cancellable = true)
	private void clientTick(Level p_151320_, BlockPos p_151321_, CallbackInfo callback) {
		if (Spawners.onSpawnerClientTick((BaseSpawner) (Object) this))
			callback.cancel();
	}

}