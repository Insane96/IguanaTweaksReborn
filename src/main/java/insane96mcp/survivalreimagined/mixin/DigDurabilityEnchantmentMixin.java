package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
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
	private static void onGetMaxLevel(ItemStack stack, int lvl, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isUnbreakingOverhaul()) {
			//Shamelessly stolen from TinkersConstruct/src/main/java/slimeknights/tconstruct/tools/modifiers/upgrades/general/ReinforcedModifier.java
			float chance;
			if (lvl < 5) {
				// formula gives 25%, 45%, 60%, 70%, 75% for first 5 levels
				chance = 0.025f * lvl * (11 - lvl);
			}
			else {
				// after level 5.5 the above formula breaks, so just do +5% per level
				// means for levels 6 to 10, you get 80%, 85%, 90%, 95%, 100%
				chance = 0.75f + (lvl - 5) * 0.05f;
			}
			cir.setReturnValue(random.nextFloat() < chance);
		}
	}
}
