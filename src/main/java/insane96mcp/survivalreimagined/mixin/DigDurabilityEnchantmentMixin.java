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
			//Shamelessly stolen from TinkersConstruct/src/main/java/slimeknights/tconstruct/tools/modifiers/upgrades/general/ReinforcedModifier.java
			float chance = 0.6f;
			if (lvl < 5) {
				// formula gives 20%, 36%, 48%, 56%, 60% for first 5 levels
				// In terms of durability, the tool lasts for x1.25, x1.56, x1.92, x2.27, x2.5 times more
				chance = 0.02f * lvl * (11 - lvl);
			}
			cir.setReturnValue(random.nextFloat() < chance);
		}
	}
}
