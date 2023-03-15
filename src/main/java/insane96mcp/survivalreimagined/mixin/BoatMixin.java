package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.misc.feature.Nerfs;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public class BoatMixin {
	@Inject(at = @At("RETURN"), method = "getGroundFriction", cancellable = true)
	public void getGroundFriction(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(Nerfs.getBoatFriction(callbackInfo.getReturnValueF()));
	}
}
