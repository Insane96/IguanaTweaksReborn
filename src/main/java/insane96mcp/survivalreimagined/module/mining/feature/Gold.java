package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;

@Label(name = "Gold", description = "Various changes for gold")
@LoadFeature(module = Modules.Ids.MINING)
public class Gold extends Feature {
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
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "gold_equipment_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Gold Equipment Crafting"), () -> this.isEnabled() && equipmentCraftingDataPack));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "gold_smelting", net.minecraft.network.chat.Component.literal("Survival Reimagined Gold Smelting"), () -> this.isEnabled() && goldSmeltingDataPack));
	}
}
