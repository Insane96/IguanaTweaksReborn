package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.event.SREventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public class CakeBlockMixin {

	@Inject(method = "eat", at = @At(value = "RETURN", ordinal = 1))
	private static void onCakeEat(LevelAccessor pLevel, BlockPos pPos, BlockState pState, Player pPlayer, CallbackInfoReturnable<InteractionResult> cir) {
		SREventFactory.onCakeEatEvent(pPlayer, pPos, pLevel);
	}
}
