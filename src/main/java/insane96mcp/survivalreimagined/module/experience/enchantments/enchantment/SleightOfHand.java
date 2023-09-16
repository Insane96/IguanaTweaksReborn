package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.ItemAttributeModifierEvent;

import java.util.UUID;

public class SleightOfHand extends Enchantment implements IDamagingEnchantment {
    public static final UUID BONUS_ATTACK_SPEED_UUID = UUID.fromString("7b0cb3a4-7a7c-4908-be8d-aadd523690d7");
    public SleightOfHand() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int pEnchantmentLevel) {
        return 3 + (pEnchantmentLevel - 1) * 10;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 20;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && !(enchantment instanceof IDamagingEnchantment) && super.checkCompatibility(enchantment);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     * @param pStack The ItemStack to test.
     */
    public boolean canEnchant(ItemStack pStack) {
        return pStack.getItem() instanceof AxeItem ? true : super.canEnchant(pStack);
    }

    public static void applyAttributeModifier(ItemAttributeModifierEvent event) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND)
            return;
        int lvl = event.getItemStack().getEnchantmentLevel(EnchantmentsFeature.RHYTHMIC_SWING.get());
        if (lvl == 0)
            return;

        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(BONUS_ATTACK_SPEED_UUID, "Rhythmic Swing enchantment", 0.15 * lvl, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
