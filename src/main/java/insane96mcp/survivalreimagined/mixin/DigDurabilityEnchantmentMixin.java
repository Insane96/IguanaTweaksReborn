package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin {
	@Inject(at = @At(value = "RETURN", ordinal = 1), method = "shouldIgnoreDurabilityDrop", cancellable = true)
	private static void onChanceToIgnoreDurabilityDrop(ItemStack stack, int lvl, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isUnbreakingOverhaul()) {
			cir.setReturnValue(random.nextFloat() < EnchantmentsFeature.unbreakingBonus(lvl));
		}
	}
}
