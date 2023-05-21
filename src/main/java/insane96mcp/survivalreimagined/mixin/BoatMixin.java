package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.movement.feature.Boats;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin {
	@Inject(at = @At("RETURN"), method = "getGroundFriction", cancellable = true)
	public void getGroundFriction(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(Boats.getBoatFriction(callbackInfo.getReturnValueF()));
	}
}
