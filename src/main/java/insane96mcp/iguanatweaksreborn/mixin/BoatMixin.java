package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.movement.Boats;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity {
	public BoatMixin(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Inject(at = @At("RETURN"), method = "getGroundFriction", cancellable = true)
	public void onGetGroundFriction(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(Boats.getBoatFriction(callbackInfo.getReturnValueF()));
	}

	@ModifyExpressionValue(method = "checkFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/vehicle/Boat$Status;ON_LAND:Lnet/minecraft/world/entity/vehicle/Boat$Status;"))
	public Boat.Status onCheckStatus(Boat.Status original) {
		if (Feature.isEnabled(Boats.class))
			return original;
		return Boat.Status.IN_AIR;
	}

	@ModifyExpressionValue(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;isRemoved()Z"))
	public boolean onCheckRemoved(boolean original) {
		if (Feature.isEnabled(Boats.class))
			return original;
		return original && this.fallDistance >= Boats.breakHeight;
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = "floatValue=40.0"))
	public float damageToBreak(float damageToBreak) {
		return 20f;
	}
}
