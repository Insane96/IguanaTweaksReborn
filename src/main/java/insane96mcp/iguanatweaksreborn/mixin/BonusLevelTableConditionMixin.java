package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.module.mining.Gold;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BonusLevelTableCondition.class)
public class BonusLevelTableConditionMixin {

	@Redirect(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"))
	public int getLevel(Enchantment enchantment, ItemStack stack) {
		int lvl = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
		int luckLvl = EnchantmentHelper.getTagEnchantmentLevel(EnchantmentsFeature.LUCK.get(), stack);
		return Gold.getFortuneLevel(Math.max(lvl, luckLvl), stack);
	}
}
