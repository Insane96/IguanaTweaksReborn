package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.world.feature.CoalFire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {

	@Inject(at = @At("RETURN"), method = "getStateForPlacement", cancellable = true)
	private void onGetStateForPlacement(BlockPlaceContext blockPlaceContext, CallbackInfoReturnable<BlockState> cir) {
		if (!CoalFire.areCampfiresUnlit())
			return;
		LevelAccessor levelaccessor = blockPlaceContext.getLevel();
		BlockPos blockpos = blockPlaceContext.getClickedPos();
		boolean flag = levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER;
		cir.setReturnValue(((CampfireBlock)(Object)this).defaultBlockState().setValue(CampfireBlock.WATERLOGGED, flag).setValue(CampfireBlock.SIGNAL_FIRE, this.isSmokeSource(levelaccessor.getBlockState(blockpos.below()))).setValue(CampfireBlock.LIT, Boolean.FALSE).setValue(CampfireBlock.FACING, blockPlaceContext.getHorizontalDirection()));
	}

	@Shadow
	protected abstract boolean isSmokeSource(BlockState blockState);
}