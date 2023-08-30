package insane96mcp.survivalreimagined.module.mining.solarium.item;

import insane96mcp.survivalreimagined.module.mining.solarium.Solarium;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SolariumAxeItem extends AxeItem {
	public SolariumAxeItem(float pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
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
