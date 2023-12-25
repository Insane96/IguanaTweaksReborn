package insane96mcp.iguanatweaksreborn.module.items.solarium.item;

import insane96mcp.iguanatweaksreborn.module.farming.hoes.IHoeCooldownModifier;
import insane96mcp.iguanatweaksreborn.module.items.solarium.Solarium;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SolariumHoeItem extends HoeItem implements IHoeCooldownModifier {
	public SolariumHoeItem(int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
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

	@Override
	public int getCooldownOnUse(int baseCooldown, Player player, Level level) {
		float calculatedSkyLightRatio = Solarium.getCalculatedSkyLightRatio(player);
		if (calculatedSkyLightRatio <= 0f)
			return baseCooldown;
		return (int) (baseCooldown - (10 * calculatedSkyLightRatio));
	}
}
