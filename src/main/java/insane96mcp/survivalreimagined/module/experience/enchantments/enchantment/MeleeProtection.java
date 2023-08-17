package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class MeleeProtection extends Enchantment {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public MeleeProtection() {
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
        return !source.is(DamageTypes.MAGIC) && !source.is(DamageTypes.INDIRECT_MAGIC) && !source.is(DamageTypes.MOB_PROJECTILE) && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypeTags.IS_FALL) && !source.is(DamageTypeTags.IS_EXPLOSION) && !source.is(DamageTypeTags.IS_PROJECTILE) ? level * 2 : 0;
    }

    public boolean checkCompatibility(Enchantment other) {
        if (other instanceof ProtectionEnchantment otherProtection) {
            return otherProtection.type == ProtectionEnchantment.Type.FALL;
        } else {
            return other instanceof MagicProtection && super.checkCompatibility(other);
        }
    }
}
