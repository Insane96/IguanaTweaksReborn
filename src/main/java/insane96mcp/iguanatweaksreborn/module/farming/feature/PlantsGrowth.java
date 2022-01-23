package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.module.farming.utils.PlantGrowthModifier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing")
public class PlantsGrowth extends Feature {

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

	public ArrayList<PlantGrowthModifier> plantsList;

	public PlantsGrowth(Module module) {
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

	public static ArrayList<PlantGrowthModifier> parsePlantsGrowthMultiplier(List<? extends String> list) {
		ArrayList<PlantGrowthModifier> plantsGrowthModifier = new ArrayList<>();
		for (String line : list) {
			PlantGrowthModifier plantGrowthMultiplier = PlantGrowthModifier.parseLine(line);
			if (plantGrowthMultiplier != null)
				plantsGrowthModifier.add(plantGrowthMultiplier);
		}
		return plantsGrowthModifier;
	}

	@SubscribeEvent
	public void cropGrowPost(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled())
			return;
		if (this.plantsList.isEmpty())
			return;
		double multiplier = 1d;
		for (PlantGrowthModifier plantGrowthModifier : this.plantsList) {
			multiplier = plantGrowthModifier.getMultiplier(event.getState().getBlock(), (Level) event.getWorld(), event.getPos());
			if (multiplier != -1d)
				break;
		}
		if (multiplier == 0d) {
			event.setResult(Event.Result.DENY);
			return;
		}
		if (multiplier == 1.0d || multiplier == -1d)
			return;
		double chance = 1d / multiplier;
		if (event.getWorld().getRandom().nextDouble() > chance)
			event.setResult(Event.Result.DENY);
	}
}