package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

@Label(name = "Iron", description = "Various changes for iron")
@LoadFeature(module = Modules.Ids.MINING)
public class Iron extends Feature {

	//TODO Maybe some kind of Soul Forge to double yields from ores
	//TODO Make iron armor require chain armor instead of leather. As much as I like the idea of using leather as a base, cows and leather are not easy to come by. Make chain armor craftable from leather+chains in anvil.
	//1 leather for boots, 2 for helmet, 3 for leggings and 4 for chestplate. Chains amount is the same as the vanilla crafting
	@Config
	@Label(name = "Farmable Iron data pack", description = """
			Enables the following changes to vanilla data pack:
			* Stone (Broken with a non Silk-Touch tool) can drop Iron Nuggets
			* Silverfish can drop Iron Nuggets""")
	public static Boolean farmableIronDataPack = true;

	@Config
	@Label(name = "Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Iron Armor requires leather armor to be crafted in an anvil
			* Iron Tools require flint tools to be crafted in an anvil""")
	public static Boolean equipmentCraftingDataPack = true;

	@Config
	@Label(name = "Iron Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting iron in a furnace takes 4x time""")
	public static Boolean ironSmeltingDataPack = true;

	@Config
	@Label(name = "Iron Generation Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Iron ore has a chance to be discarded when exposed to air (60% for large veins, 40% for small)""")
	public static Boolean ironGenerationDataPack = true;

	public Iron(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "farmable_iron", net.minecraft.network.chat.Component.literal("Survival Reimagined Farmable Iron"), () -> this.isEnabled() && farmableIronDataPack));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "iron_equipment_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Iron Equipment Crafting"), () -> this.isEnabled() && equipmentCraftingDataPack));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "iron_smelting", net.minecraft.network.chat.Component.literal("Survival Reimagined Iron Smelting"), () -> this.isEnabled() && ironSmeltingDataPack));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "iron_generation", net.minecraft.network.chat.Component.literal("Survival Reimagined Iron Generation"), () -> this.isEnabled() && ironGenerationDataPack));
	}

	private static final String path = "iron/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {

	}
}
