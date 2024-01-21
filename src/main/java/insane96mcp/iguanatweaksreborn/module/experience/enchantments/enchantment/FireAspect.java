package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.jetbrains.annotations.NotNull;

public class FireAspect extends Enchantment implements IEnchantmentTooltip {
    public FireAspect() {
        super(Rarity.RARE, EnchantmentsFeature.WEAPONS_CATEGORY, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 10 + 20 * (pEnchantmentLevel - 1);
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return super.getMinCost(pEnchantmentLevel) + 50;
    }

    public int getMaxLevel() {
        return 2;
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity attacker, @NotNull Entity target, int lvl) {
        int ticks = this.secondsOnFirePerLevel() * lvl * 20 + 10;
        if (target instanceof LivingEntity)
            ticks = ProtectionEnchantment.getFireAfterDampener((LivingEntity) target, ticks);

        if (target.getRemainingFireTicks() < ticks)
            target.setRemainingFireTicks(ticks);
    }

    public int secondsOnFirePerLevel() {
        return 4;
    }

    @Override
    public Component getTooltip(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        return Component.translatable(this.getDescriptionId() + ".tooltip", this.secondsOnFirePerLevel() * lvl).withStyle(ChatFormatting.DARK_PURPLE);
    }
}
