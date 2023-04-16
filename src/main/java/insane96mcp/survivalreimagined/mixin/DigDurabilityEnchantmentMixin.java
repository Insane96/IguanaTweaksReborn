package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "getMaxLevel", cancellable = true)
	private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
		if (EnchantmentsFeature.isUnbreakingOverhaulEnabled())
			cir.setReturnValue(1);
	}

	@Inject(at = @At("RETURN"), method = "getMinCost", cancellable = true)
	private void onGetMinCost(int lvl, CallbackInfoReturnable<Integer> cir) {
		if (EnchantmentsFeature.isUnbreakingOverhaulEnabled())
			cir.setReturnValue(13);
	}

	@Inject(at = @At("RETURN"), method = "getMaxCost", cancellable = true)
	private void onGetMaxCost(int lvl, CallbackInfoReturnable<Integer> cir) {
		if (EnchantmentsFeature.isUnbreakingOverhaulEnabled())
			cir.setReturnValue(63);
	}

	/*@Inject(at = @At("HEAD"), method = "shouldIgnoreDurabilityDrop", cancellable = true)
	private static void onShouldIgnoreDurabilityDrop(ItemStack stack, int lvl, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
		if (!EnchantmentsFeature.isUnbreakingOverhaulEnabled())
			return;

		cir.setReturnValue(random.nextInt(lvl + 1) > 0);
	}*/
}
