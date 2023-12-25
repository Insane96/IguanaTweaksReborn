package insane96mcp.survivalreimagined.module.items.solarium.item;

import insane96mcp.shieldsplus.setup.SPItems;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.module.items.solarium.Solarium;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class SolariumShield extends SPShieldItem {
	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("solarium", 401, Solarium.SOLARIUM_BALL, 8, Rarity.COMMON);
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

	@Override
	public double getBlockedDamage(ItemStack stack, @Nullable LivingEntity entity, Level level) {
		if (entity == null)
			return super.getBlockedDamage(stack, null, level);
		float calculatedLightRatio = Solarium.getCalculatedSkyLightRatio(entity);
		return super.getBlockedDamage(stack, entity, level) + (2f * calculatedLightRatio);
	}

	public static RegistryObject<SPShieldItem> registerShield(String id) {
		Item.Properties properties = new Item.Properties().durability(SHIELD_MATERIAL.durability).rarity(SHIELD_MATERIAL.rarity);
		RegistryObject<SPShieldItem> shield = SRRegistries.ITEMS.register(id, () -> new SolariumShield(properties));
		SPItems.SHIELDS.add(shield);
		return shield;
	}
}
