package insane96mcp.iguanatweaksreborn.module.combat.criticalhits;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import net.minecraft.world.entity.EquipmentSlot;

public class CriticalEnchantment extends BonusDamageEnchantment {
    public CriticalEnchantment() {
        super(Rarity.UNCOMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public static float getCritAmount(int lvl, float baseCrit) {
        float critBonus = baseCrit - 1f;
        return lvl * critBonus + baseCrit;
    }

    @Override
    public float getDamageBonusPerLevel() {
        return 0;
    }
}
