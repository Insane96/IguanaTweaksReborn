package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerTileEntity.class)
public class MobSpawnerTileEntityMixin {

	@Inject(at = @At("HEAD"), method = "tick()V", cancellable = true)
	private void tick(CallbackInfo callback) {
		Modules.misc.tempSpawner.onTick((MobSpawnerTileEntity) (Object) this);
	}

}
