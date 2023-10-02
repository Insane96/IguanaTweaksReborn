package insane96mcp.survivalreimagined.module.experience.enchantments.enchantment;

import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import java.util.UUID;

public class GravityDefying extends Enchantment {
    public static final UUID GRAVITY_MODIFIER_UUID = UUID.fromString("7567b3ad-a4c6-4700-8bf0-cd8c4356c155");
    public GravityDefying() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 30;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 20;
    }

    public static final double[] GRAVITY_MODIFIERS = new double[] { 0d, 0.55d, 0.7d, 0.8d };

    public static void applyAttributeModifier(ItemAttributeModifierEvent event) {
        if (event.getSlotType() != EquipmentSlot.FEET)
            return;
        int lvl = event.getItemStack().getEnchantmentLevel(EnchantmentsFeature.GRAVITY_DEFYING.get());
        if (lvl <= 0)
            return;
        if (lvl >= GRAVITY_MODIFIERS.length)
            lvl = GRAVITY_MODIFIERS.length - 1;

        event.addModifier(ForgeMod.ENTITY_GRAVITY.get(), new AttributeModifier(GRAVITY_MODIFIER_UUID, "Gravity Defying enchantment", -GRAVITY_MODIFIERS[lvl], AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    public static void applyFallDamageReduction(LivingFallEvent event) {
        int lvl = event.getEntity().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(EnchantmentsFeature.GRAVITY_DEFYING.get());
        if (lvl <= 0)
            return;

        event.setDistance((float) (event.getDistance() - ((1f - GRAVITY_MODIFIERS[lvl]) * event.getDistance())));
    }
}
