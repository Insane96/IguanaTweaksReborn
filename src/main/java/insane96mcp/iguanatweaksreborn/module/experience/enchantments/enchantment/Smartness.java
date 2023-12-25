package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;

public class Smartness extends Enchantment {
    static EnchantmentCategory WEAPON_AND_DIGGER = EnchantmentCategory.create("weapon_and_digger", item -> item instanceof SwordItem || item instanceof DiggerItem);

    public Smartness() {
        super(Rarity.RARE, WEAPON_AND_DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public int getMinCost(int lvl) {
        return 15 + (lvl - 1) * 9;
    }

    public int getMaxCost(int lvl) {
        return super.getMinCost(lvl) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && !(enchantment instanceof LootBonusEnchantment);
    }

    public static int getIncreasedExperience(RandomSource random, int lvl, int experience) {
        return MathHelper.getAmountWithDecimalChance(random, experience * (1 + lvl * .5f));
    }
}
