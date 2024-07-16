package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.world.Fluids;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FlowingFluid.class)
public class FlowingFluidMixin {
	@ModifyExpressionValue(method = "getFlow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;isSolidFace(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"))
	public boolean onCheckFlowingFluid(boolean original) {
		return Fluids.shouldWaterPushWhenNoBlocksAround() || original;
	}
}
