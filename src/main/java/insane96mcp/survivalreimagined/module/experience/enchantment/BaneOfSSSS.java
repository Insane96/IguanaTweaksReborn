package insane96mcp.survivalreimagined.module.experience.enchantment;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BaneOfSSSS extends Enchantment {
    public BaneOfSSSS() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int lvl) {
        return 5 + (lvl - 1) * 8;
    }

    @Override
    public int getMaxCost(int lvl) {
        return this.getMinCost(lvl) + 20;
    }

    @Override
    public float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return mobType == MobType.ARTHROPOD ? (float)level * 2.5F : 0.0F;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && super.checkCompatibility(enchantment);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return EnchantmentsFeature.isBaneOfSSSSSEnabled() && (stack.getItem() instanceof AxeItem || super.canEnchant(stack));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return EnchantmentsFeature.isBaneOfSSSSSEnabled() && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public void doPostAttack(LivingEntity livingEntity, Entity entity, int lvl) {
        if (entity instanceof LivingEntity livingentity) {
            if (lvl > 0 && (livingentity.getMobType() == MobType.ARTHROPOD || livingentity instanceof Creeper)) {
                int i = 20 + livingEntity.getRandom().nextInt(10 * lvl);
                livingentity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, i, 3));
            }
        }
    }
}
