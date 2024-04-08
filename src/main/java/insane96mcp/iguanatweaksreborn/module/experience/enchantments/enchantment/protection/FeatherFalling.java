package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FeatherFalling extends Enchantment implements IProtectionEnchantment {
    public FeatherFalling() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET });
    }

    @Override
    public int getMinCost(int lvl) {
        return 5 + (lvl - 1) * 6;
    }

    @Override
    public int getMaxCost(int lvl) {
        return this.getMinCost(lvl) + 6;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && EnchantmentsFeature.replaceProtectionEnchantments;
    }

    @Override
    public float getDamageReduction(int lvl) {
        return 0.16f * lvl;
    }

    @Override
    public boolean isSourceReduced(DamageSource source) {
        return source.is(DamageTypeTags.IS_FALL);
    }
}
