package insane96mcp.survivalreimagined.module.mining.solarium.item;

import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.module.mining.solarium.Solarium;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public class SolariumShield extends SPShieldItem {
	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("solarium", 4.5d, 401, Solarium.SOLARIUM_BALL, 8, Rarity.COMMON);
	public SolariumShield(Properties p_43089_) {
		super(SHIELD_MATERIAL, p_43089_);
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		Solarium.healGear(pStack, pEntity, pLevel);
	}

	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !ItemStack.isSameItem(oldStack, newStack);
	}

	public static RegistryObject<SPShieldItem> registerShield(String id) {
		Item.Properties properties = new Item.Properties().durability(SHIELD_MATERIAL.durability).rarity(SHIELD_MATERIAL.rarity);
		RegistryObject<SPShieldItem> shield = SRRegistries.ITEMS.register(id, () -> new SolariumShield(properties));
		SPItems.SHIELDS.add(shield);
		return shield;
	}
}
