package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.jetbrains.annotations.NotNull;

public abstract class ITRProtectionEnchantment extends Enchantment implements IProtectionEnchantment {
    public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ITRProtectionEnchantment(Rarity rarity) {
        super(rarity, EnchantmentCategory.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMinCost(int lvl) {
        return this.getBaseCost() + (lvl - 1) * this.getCostPerLevel();
    }

    @Override
    public int getMaxCost(int lvl) {
        return this.getMinCost(lvl) + this.getCostPerLevel();
    }

    public abstract int getBaseCost();
    public abstract int getCostPerLevel();

    @Override
    public abstract int getMaxLevel();

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment other) {
        if (!super.checkCompatibility(other)
                || (other instanceof ProtectionEnchantment && other != Enchantments.FALL_PROTECTION)
                || (other instanceof ITRProtectionEnchantment && other != EnchantmentsFeature.FEATHER_FALLING.get()))
            return false;
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && EnchantmentsFeature.replaceProtectionEnchantments;
    }

    @Override
    public float getDamageReduction(int lvl) {
        return this.getDamageReductionPerLevel() * lvl;
    }

    public abstract float getDamageReductionPerLevel();

    @Override
    public abstract boolean isSourceReduced(DamageSource source);
}
