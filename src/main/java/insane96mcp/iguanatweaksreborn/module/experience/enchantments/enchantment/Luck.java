package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

public class Luck extends LootBonusEnchantment {
    public static final TagKey<Item> ACCEPTS_ENCHANTMENT = ITRItemTagsProvider.create("enchanting/accepts_luck");
    static final EnchantmentCategory CATEGORY = EnchantmentCategory.create("luck", item -> item.builtInRegistryHolder().is(ACCEPTS_ENCHANTMENT));
    public Luck() {
        super(Rarity.RARE, CATEGORY, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && EnchantmentsFeature.replaceLuckEnchantments;
    }
}
