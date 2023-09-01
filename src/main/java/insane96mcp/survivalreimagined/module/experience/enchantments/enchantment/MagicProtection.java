package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class MagicProtection extends Enchantment implements IProtectionEnchantment {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public MagicProtection() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 8;
    }

    @Override
    public int getDamageProtection(int level, DamageSource source) {
        return source.is(DamageTypes.MAGIC) || source.is(DamageTypes.INDIRECT_MAGIC) ? level * 2 : 0;
    }

    public boolean checkCompatibility(Enchantment other) {
        if (other instanceof ProtectionEnchantment otherProtection) {
            return otherProtection.type == ProtectionEnchantment.Type.FALL;
        } else {
            return !(other instanceof IProtectionEnchantment) && super.checkCompatibility(other);
        }
    }

    public static void reduceBadEffectsDuration(LivingEntity entity, MobEffectInstance effectInstance) {
        if (effectInstance.getEffect().isBeneficial()
                || effectInstance.getEffect().isInstantenous())
            return;

        int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentsFeature.MAGIC_PROTECTION.get(), entity);
        if (level == 0)
            return;

        effectInstance.duration -= effectInstance.duration * (0.05f * level);
    }
}
