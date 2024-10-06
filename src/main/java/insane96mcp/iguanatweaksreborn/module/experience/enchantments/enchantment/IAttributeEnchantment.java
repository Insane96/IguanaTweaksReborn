package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import net.minecraftforge.event.ItemAttributeModifierEvent;

public interface IAttributeEnchantment {
    void applyAttributeModifier(ItemAttributeModifierEvent event, int enchantmentLvl);
}
