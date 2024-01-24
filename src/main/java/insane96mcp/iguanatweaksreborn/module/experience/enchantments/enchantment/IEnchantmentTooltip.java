package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IEnchantmentTooltip {
    Component getTooltip(ItemStack stack, int lvl);
}
