package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.ReplaceDropModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

import java.util.List;

@Label(name = "Iron", description = "Various changes for iron")
@LoadFeature(module = Modules.Ids.MINING)
public class Iron extends Feature {

	//Maybe some kind of Soul Forge to double yields from ores

	@Config
	@Label(name = "Farmable Iron data pack", description = """
			Enables the following changes to vanilla data pack:
			* Stone (Broken with a non Silk-Touch tool) can drop Iron Nuggets
			* Silverfish can drop Iron Nuggets""")
	public static Boolean farmableIronDataPack = true;

	@Config
	@Label(name = "Armor Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Iron Armor requires leather armor to be crafted
			""")
	public static Boolean armorCraftingDataPack = true;

	public Iron(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "farmable_iron", net.minecraft.network.chat.Component.literal("Survival Reimagined Farmable Iron"), () -> farmableIronDataPack));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "iron_armor_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Iron Armor Crafting"), () -> armorCraftingDataPack));
	}

	private static final String path = "mining_progression/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "raw_iron_to_nuggets", new ReplaceDropModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.IRON_ORE).build()},
				Items.RAW_IRON,
				Items.IRON_NUGGET,
				List.of(1f, 2f, 5f, 9f, 12f)
		));
		provider.add(path + "deepslate_raw_iron_to_nuggets", new ReplaceDropModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.DEEPSLATE_IRON_ORE).build()},
				Items.RAW_IRON,
				Items.IRON_NUGGET,
				List.of(1f, 2f, 5f, 9f, 12f)
		));
	}
}
