package insane96mcp.iguanatweaksreborn.module.items.solarium.item;

import insane96mcp.iguanatweaksreborn.module.items.solarium.Solarium;
import net.minecraft.world.entity.Entity;
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

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !ItemStack.isSameItem(oldStack, newStack);
	}
}
