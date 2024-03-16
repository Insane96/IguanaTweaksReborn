package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import insane96mcp.insanelib.world.enchantments.IEnchantmentTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Knockback extends Enchantment implements IEnchantmentTooltip {
    public static TagKey<Item> ACCEPTS_ENCHANTMENT = ITRItemTagsProvider.create("enchanting/accepts_knockback");
    public static EnchantmentCategory CATEGORY = EnchantmentCategory.create("accepts_knockback", item -> item.builtInRegistryHolder().is(ACCEPTS_ENCHANTMENT));
    public Knockback() {
        super(Rarity.UNCOMMON, CATEGORY, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 5 + 20 * (pEnchantmentLevel - 1);
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

    @Override
    public Component getTooltip(ItemStack stack, int lvl) {
        return Component.translatable(this.getDescriptionId() + ".tooltip", BonusDamageEnchantment.getDamageBonusRatio(stack) * lvl).withStyle(ChatFormatting.DARK_PURPLE);
    }
}
