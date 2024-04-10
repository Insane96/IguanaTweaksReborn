package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage;

import net.minecraft.world.entity.EquipmentSlot;

public class Sharpness extends BonusDamageEnchantment {
    public Sharpness() {
        super(Rarity.UNCOMMON, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public float getDamageBonusPerLevel() {
        return 0.6f;
    }
}
