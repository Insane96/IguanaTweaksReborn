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

	@Inject(at = @At("HEAD"), method = "serverTick")
	private void serverTick(ServerLevel p_151312_, BlockPos p_151313_, CallbackInfo callback) {
		Spawners.onSpawnerServerTick((BaseSpawner) (Object) this);
	}

	@Inject(at = @At("HEAD"), method = "clientTick")
	private void serverTick(Level p_151320_, BlockPos p_151321_, CallbackInfo callback) {
		Spawners.onClientTick((BaseSpawner) (Object) this);
	}

}