package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class Sharpness extends BonusDamageEnchantment {
    public Sharpness() {
        super(Rarity.UNCOMMON, EnchantmentsFeature.ITR_WEAPONS, new EquipmentSlot[]{ EquipmentSlot.MAINHAND });
    }

    @Override
    public float getDamageBonus(ItemStack stack, int lvl) {
        return 0.75f * lvl * this.getDamageBonusRatio(stack);
    }
}
