package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.movement.Boats;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin {
	@Inject(at = @At("RETURN"), method = "getGroundFriction", cancellable = true)
	public void onGetGroundFriction(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(Boats.getBoatFriction(callbackInfo.getReturnValueF()));
	}

	/*@Inject(at = @At("RETURN"), method = "hasEnoughSpaceFor", cancellable = true)
	public void onHasEnoughSpaceFor(Entity other, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(cir.getReturnValueZ() || other instanceof AbstractHorse);
	}*/
}
