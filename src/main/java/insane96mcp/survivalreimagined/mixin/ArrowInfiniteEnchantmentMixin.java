package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowInfiniteEnchantment.class)
public class ArrowInfiniteEnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "getMinCost", cancellable = true)
	private void onGetMinCost(int lvl, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(12 + (lvl - 1) * 12);
	}

	@Inject(at = @At("RETURN"), method = "getMaxCost", cancellable = true)
	private void onGetMaxCost(int lvl, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(50);
	}
}
