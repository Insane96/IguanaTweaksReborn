package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IEnchantmentTooltip {
    Component getTooltip(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl);
}
