package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Critical extends Enchantment implements IDamagingEnchantment {
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
        return !(enchantment instanceof DamageEnchantment) && !(enchantment instanceof IDamagingEnchantment) && super.checkCompatibility(enchantment);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     * @param pStack The ItemStack to test.
     */
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof AxeItem ? true : super.canEnchant(pStack);
    }

    public static float getCritAmount(int lvl, float baseCrit) {
        return lvl * (baseCrit - 1) + 1 + (baseCrit - 1);
    }

    @Override
    public void doPostAttack(LivingEntity attacker, Entity entity, int lvl) {
        if (lvl > 0 && attacker instanceof ServerPlayer player) {
            boolean isCrit = player.getAttackStrengthScale(0.5F) > 0.9f && player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && entity instanceof LivingEntity;
            if (isCrit)
                player.magicCrit(entity);
        }
    }
}
