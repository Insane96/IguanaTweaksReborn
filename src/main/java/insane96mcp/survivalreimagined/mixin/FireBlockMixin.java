package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.world.coalfire.CoalFire;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@Inject(at = @At("RETURN"), method = "getFireTickDelay", cancellable = true)
	private static void setFireTickDelay(RandomSource p_221149_, CallbackInfoReturnable<Integer> cir) {
		if (CoalFire.changeFireSpreadSpeed())
			cir.setReturnValue((int) (cir.getReturnValue() / CoalFire.fireSpreadSpeedMultiplier));
	}
}
