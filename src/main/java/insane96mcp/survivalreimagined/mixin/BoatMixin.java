package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.movement.feature.Boats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Boat.class)
public abstract class BoatMixin {
	@Shadow
	private boolean inputUp;

	@Shadow @Nullable public abstract LivingEntity getControllingPassenger();

	@Inject(at = @At("RETURN"), method = "getGroundFriction", cancellable = true)
	public void getGroundFriction(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(Boats.getBoatFriction(callbackInfo.getReturnValueF()));
	}
}
