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

@Label(name = "Other Materials", description = "Various changes for all the materials that don't require a new feature because too low changes.")
@LoadFeature(module = Modules.Ids.MINING)
public class OtherMaterials extends Feature {
	@Config
	@Label(name = "Copper Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting copper in a furnace takes 4x time""")
	public static Boolean copperSmeltingDataPack = true;
	@Config
	@Label(name = "Anvil Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Diamond Gear requires Gold gear to be crafted in an anvil
			* Gold Gear requires Flint/Leather gear to be crafted in an anvil
			* Iron Gear requires Stone / Chained Copper gear to be crafted in an anvil
			* Buckets and Shears require an anvil to be crafted""")
	public static Boolean anvilEquipmentCrafting = true;
	@Config
	@Label(name = "Alloying Netherite Data Pack", description = "Enables a data pack that adds a recipe to alloy netherite in a Blast or Soul furnace, requiring less materials")
	public static Boolean alloyingNetheriteDataPack = true;

	public OtherMaterials(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_smelting", net.minecraft.network.chat.Component.literal("Survival Reimagined Copper Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperSmeltingDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "anvil_equipment_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Anvil Equipment Crafting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && anvilEquipmentCrafting));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "alloying_netherite", net.minecraft.network.chat.Component.literal("Survival Reimagined Netherite Alloy"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && alloyingNetheriteDataPack));
	}
}
