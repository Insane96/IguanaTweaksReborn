package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.event.SREventFactory;
import insane96mcp.survivalreimagined.module.world.feature.Fire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1), method = "tryCatchFire", remap = false)
	void onBlockBurnt(Level level, BlockPos pos, int p_53434_, RandomSource random, int p_53436_, Direction face, CallbackInfo ci) {
		SREventFactory.onBlockBurnt(level, pos, level.getBlockState(pos));
	}

	@Inject(at = @At("RETURN"), method = "getFireTickDelay", cancellable = true)
	private static void setFireTickDelay(RandomSource p_221149_, CallbackInfoReturnable<Integer> cir) {
		if (Fire.changeFireSpreadSpeed())
			cir.setReturnValue((int) (cir.getReturnValue() / Fire.fireSpreadSpeedMultiplier));
	}
}
