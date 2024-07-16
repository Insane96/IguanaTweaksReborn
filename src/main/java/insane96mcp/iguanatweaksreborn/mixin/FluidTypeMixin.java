package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.world.Fluids;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidType.class)
public class FluidTypeMixin {
	@Inject(at = @At("RETURN"), method = "motionScale", cancellable = true, remap = false)
	public void onMotionScale(Entity entity, CallbackInfoReturnable<Double> cir) {
		if ((Object) this == ForgeMod.WATER_TYPE.get()) {
			cir.setReturnValue(Fluids.waterPushForce);
		}
	}
}
