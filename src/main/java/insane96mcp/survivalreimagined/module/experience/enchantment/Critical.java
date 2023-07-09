package insane96mcp.survivalreimagined.module.experience.enchantment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Critical extends Enchantment {
    public Critical() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int pEnchantmentLevel) {
        return 3 + (pEnchantmentLevel - 1) * 10;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 20;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && !(enchantment instanceof WaterCoolant) && super.checkCompatibility(enchantment);
    }

    public static float getCritAmount(int lvl, float baseCrit) {
        return lvl * (baseCrit - 1) + 1;
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity entity, int lvl) {
        if (lvl > 0 && attacker instanceof ServerPlayer player)
            player.magicCrit(entity);
    }
}
