package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.world.coalfire.CoalFire;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

	@Inject(at = @At("RETURN"), method = "getStateForPlacement", cancellable = true)
	private void onGetStateForPlacement(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<BlockState> cir) {
		if (!CoalFire.areCampfiresUnlit())
			return;
		cir.setReturnValue(cir.getReturnValue().setValue(CampfireBlock.LIT, Boolean.FALSE));
	}
}