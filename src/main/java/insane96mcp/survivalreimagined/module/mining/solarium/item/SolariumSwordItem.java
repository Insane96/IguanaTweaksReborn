package insane96mcp.survivalreimagined.module.mining.solarium.item;

import com.google.common.collect.Multimap;
import insane96mcp.survivalreimagined.module.mining.solarium.Solarium;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;

public class SolariumSwordItem extends SwordItem {
	public SolariumSwordItem(int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
		super(Solarium.ITEM_TIER, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		Solarium.healGear(pStack, pEntity, pLevel);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return super.getAttributeModifiers(slot, stack);
	}
}
