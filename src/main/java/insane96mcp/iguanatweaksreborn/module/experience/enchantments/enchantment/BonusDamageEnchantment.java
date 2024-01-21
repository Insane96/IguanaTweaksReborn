package insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.stream.Stream;

//TODO Expand
//Add a getDamageBonus method, making it stack sensitive
//Add a tooltip for bonus damage against x
//Add
public abstract class BonusDamageEnchantment extends Enchantment {

    protected BonusDamageEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public int getMinCost(int lvl) {
        return 4 + (lvl - 1) * 8;
    }

    @Override
    public int getMaxCost(int lvl) {
        return this.getMinCost(lvl) + 20;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && !(pOther instanceof BonusDamageEnchantment);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public float getDamageBonus(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        return this.getDamageBonus(stack, lvl);
    }

    public abstract float getDamageBonus(ItemStack stack, int lvl);

    public float getDamageBonusRatio(ItemStack stack) {
        if (!(stack.getItem() instanceof TieredItem))
            return 0f;
        float baseDamage = 0f;
        Stream<AttributeModifier> weaponAttributeModifier = stack.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().filter(attributeModifier -> attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION);
        for (AttributeModifier attributeModifier : weaponAttributeModifier.toList()) {
            baseDamage += (float) attributeModifier.getAmount();
        }
        return baseDamage / 5f;
    }

    public Component getBonusDamageTooltip(LivingEntity attacker, LivingEntity target, ItemStack stack, int lvl) {
        return Component.translatable(this.getDescriptionId() + ".affected", IguanaTweaksReborn.ONE_DECIMAL_FORMATTER.format(this.getDamageBonus(stack, lvl))).withStyle(ChatFormatting.DARK_PURPLE);
    }
}
