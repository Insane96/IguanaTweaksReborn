package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.data.lootmodifier.ReplaceDropModifier;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

@Label(name = "Mining Progression", description = "Various progression changes for mining")
@LoadFeature(module = Modules.Ids.MINING)
public class MiningProgression extends Feature {

	public MiningProgression(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	private static final String path = "mining_progression/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "raw_iron_to_nuggets", new ReplaceDropModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.IRON_ORE).build()},
				Items.RAW_IRON,
				Items.IRON_NUGGET
		));
		provider.add(path + "deepslate_raw_iron_to_nuggets", new ReplaceDropModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.DEEPSLATE_IRON_ORE).build()},
				Items.RAW_IRON,
				Items.IRON_NUGGET
		));
	}
}
