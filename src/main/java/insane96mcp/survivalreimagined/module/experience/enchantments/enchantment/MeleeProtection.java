package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class MeleeProtection extends Enchantment implements IProtectionEnchantment {
    public static final UUID[] ATTACK_SPEED_MODIFIER_UUIDS = new UUID[] {
            UUID.fromString("2f42e9bd-0537-403b-96b1-2a1d67029729"),
            UUID.fromString("5455c8d5-2e83-4da2-a698-2aa4333e8347"),
            UUID.fromString("f6adf83d-cc4e-48db-83b1-b5b942214353"),
            UUID.fromString("ab9acb05-1838-473a-bc87-c0832503edaa")
    };
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
            return !(other instanceof IProtectionEnchantment) && super.checkCompatibility(other);
        }
    }

    public static void applyAttributeModifier(ItemAttributeModifierEvent event) {
        if (!(event.getItemStack().getItem() instanceof ArmorItem armorItem))
            return;
        if (event.getSlotType() != armorItem.getEquipmentSlot())
            return;
        int lvl = event.getItemStack().getEnchantmentLevel(EnchantmentsFeature.MELEE_PROTECTION.get());
        if (lvl == 0)
            return;

        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER_UUIDS[event.getSlotType().getIndex()], "Melee protection enchantment", 0.025d * lvl, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
