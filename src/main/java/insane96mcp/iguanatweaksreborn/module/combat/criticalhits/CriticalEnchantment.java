package insane96mcp.iguanatweaksreborn.module.combat.criticalhits;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.IAttributeEnchantment;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class CriticalEnchantment extends BonusDamageEnchantment implements IAttributeEnchantment {
    public static final UUID CHANCE_UUID = UUID.fromString("79b6758b-8896-4822-a1c9-0c39c6d1401c");
    public static final UUID DAMAGE_UUID = UUID.fromString("3530d782-5c45-49d9-94d8-1da3c206bb7c");

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

    @Override
    public void applyAttributeModifier(ItemAttributeModifierEvent event, int enchantmentLvl) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND)
            return;
        event.addModifier(CriticalRework.CHANCE_ATTRIBUTE.get(), new AttributeModifier(CHANCE_UUID, "Critical Enchantment Modifier", CriticalRework.enchantmentChance * enchantmentLvl, AttributeModifier.Operation.ADDITION));
        event.addModifier(CriticalRework.DAMAGE_ATTRIBUTE.get(), new AttributeModifier(DAMAGE_UUID, "Critical Enchantment Modifier", CriticalRework.enchantmentBonusDamage * enchantmentLvl, AttributeModifier.Operation.ADDITION));
    }
}
