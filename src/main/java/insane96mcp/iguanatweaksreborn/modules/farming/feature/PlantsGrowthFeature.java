package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.modules.farming.classutils.PlantGrowthMultiplier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.utils.LogHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants growing")
public class PlantsGrowthFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> plantsListConfig;

	private static final List<String> plantsListDefault = Arrays.asList(
			"minecraft:sugar_cane,2.5",
			"minecraft:cactus,2.5",
			"minecraft:cocoa,3.0",
			"minecraft:nether_wart,3.0",
			"minecraft:chorus_flower,3.0",
			"#minecraft:saplings,2.5",
			"minecraft:melon_stem,3.0",
			"minecraft:pumpkin_stem,3.0",
			"minecraft:sweet_berry_bush,2.0",
			"minecraft:kelp,2.5",
			"minecraft:bamboo,2.5"
	);

	public ArrayList<PlantGrowthMultiplier> plantsList;

	public PlantsGrowthFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		plantsListConfig = Config.builder
				.comment("A list of blocks that will take more time to grow and the multiplier that increases the time to grow. Format is 'modid:blockid,multiplier' or '#modid:blocktag,multiplier'.")
				.defineList("Plants Growth Multiplier", plantsListDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.plantsList = parsePlantsGrowthMultiplier(this.plantsListConfig.get());
	}

	public static ArrayList<PlantGrowthMultiplier> parsePlantsGrowthMultiplier(List<? extends String> list) {
		ArrayList<PlantGrowthMultiplier> plantsGrowthMultiplier = new ArrayList<>();
		for (String line : list) {
			PlantGrowthMultiplier plantGrowthMultiplier = PlantGrowthMultiplier.parseLine(line);
			if (plantGrowthMultiplier != null)
				plantsGrowthMultiplier.add(plantGrowthMultiplier);
		}
		return plantsGrowthMultiplier;
	}

	@SubscribeEvent
	public void cropGrowPost(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled())
			return;
		if (event.getState().getBlock().getRegistryName().equals(new ResourceLocation("minecraft:air"))) {
			event.getWorld().setBlockState(event.getPos(), Blocks.BEDROCK.getDefaultState(), 3);
			LogHelper.info("pos: " + event.getPos());
			return;
		}
		if (this.plantsList.isEmpty())
			return;
		BlockState state = event.getState();
		double multiplier = 1d;
		for (PlantGrowthMultiplier plantGrowthMultiplier : this.plantsList) {
			if (plantGrowthMultiplier.matchesBlock(state.getBlock())) {
				multiplier = plantGrowthMultiplier.multiplier;
				break;
			}
		}
		if (multiplier == 1.0d)
			return;
		double chance = 1d / multiplier;
		if (event.getWorld().getRandom().nextDouble() > chance)
			event.setResult(Event.Result.DENY);
	}
}
