package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.insanelib.InsaneLib;
import insane96mcp.insanelib.world.enchantments.IEnchantmentTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.jetbrains.annotations.NotNull;

public abstract class ITRProtectionEnchantment extends Enchantment implements IProtectionEnchantment, IEnchantmentTooltip {
    public ITRProtectionEnchantment(Rarity rarity) {
        super(rarity, EnchantmentCategory.ARMOR, EnchantmentsFeature.ARMOR_SLOTS);
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
        return super.checkCompatibility(other) && !(other instanceof IProtectionEnchantment) && !(other instanceof ProtectionEnchantment);
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

    @Override
    public Component getTooltip(ItemStack stack, int lvl) {
        return Component.translatable(this.getDescriptionId() + ".tooltip", InsaneLib.ONE_DECIMAL_FORMATTER.format(this.getDamageReduction(lvl) * 100f)).withStyle(ChatFormatting.DARK_PURPLE);
    }
}
