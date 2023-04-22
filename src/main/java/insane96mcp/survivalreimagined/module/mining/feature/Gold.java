package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Gold", description = "Various changes for gold")
@LoadFeature(module = Modules.Ids.MINING)
public class Gold extends Feature {

	@Config
	@Label(name = "Lucky Gold", description = "Changes Gold tools to have an innate Fortune/Looting level and changes the harvest level to be like stone tools.")
	public static Boolean luckyGold = true;

	@Config
	@Label(name = "Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Gold Armor requires leather armor to be crafted in an anvil
			* Gold Tools require flint tools to be crafted in an anvil""")
	public static Boolean equipmentCraftingDataPack = true;

	@Config
	@Label(name = "Gold Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting gold in a furnace takes 4x time""")
	public static Boolean goldSmeltingDataPack = true;

	public Gold(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "gold_equipment_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Gold Equipment Crafting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && equipmentCraftingDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "gold_smelting", net.minecraft.network.chat.Component.literal("Survival Reimagined Gold Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && goldSmeltingDataPack));
	}

	@SubscribeEvent
	public void onLootingCheck(LootingLevelEvent event) {
		if (!this.isEnabled()
				|| !luckyGold
				|| event.getDamageSource() == null
				|| !(event.getDamageSource().getEntity() instanceof LivingEntity livingEntity))
			return;

		ItemStack stack = livingEntity.getMainHandItem();
		if (stack.getItem() instanceof SwordItem swordItem && swordItem.getTier() == Tiers.GOLD) {
			if (event.getLootingLevel() < 1)
				event.setLootingLevel(1);
		}
	}

	public static int getFortuneLevel(int prev, ItemStack itemStack) {
		if(!isEnabled(Gold.class)
				|| !luckyGold
				|| !(itemStack.getItem() instanceof TieredItem tieredItem)
				|| tieredItem.getTier() != Tiers.GOLD
				|| prev >= 1)
			return prev;

		return 1;
	}

	public static Tier getEffectiveTier(Tier originalTier) {
		if(!isEnabled(Gold.class)
				|| !luckyGold
				|| originalTier != Tiers.GOLD)
			return originalTier;

		return Tiers.STONE;
	}

}
