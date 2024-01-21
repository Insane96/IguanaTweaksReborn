package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class Knockback extends Enchantment implements IEnchantmentTooltip {
    public Knockback() {
        super(Rarity.UNCOMMON, EnchantmentsFeature.WEAPONS_CATEGORY, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
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
    public Component getTooltip(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        return Component.translatable(this.getDescriptionId() + ".tooltip", lvl).withStyle(ChatFormatting.DARK_PURPLE);
    }
}
