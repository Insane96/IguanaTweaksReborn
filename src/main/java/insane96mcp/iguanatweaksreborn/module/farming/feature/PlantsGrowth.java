package insane96mcp.iguanatweaksreborn.module.farming.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.utils.PlantGrowthModifier;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends ITFeature {
	/*private static final List<String> plantsListDefault = Arrays.asList(
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
	);*/

	public static ArrayList<PlantGrowthModifier> plantsList = new ArrayList<>(Arrays.asList(
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:sugar_cane", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:cactus", 2.5d, 1.5d, 10, 1d, new ArrayList<>(Arrays.asList(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_desert")
			)), 4d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:cocoa", 3d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:nether_wart", 3d, 1d, 15, 1d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:chorus_flower", 3d, 1d, 15, 1d),
			new PlantGrowthModifier(IdTagMatcher.Type.TAG, "minecraft:saplings", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:melon_stem", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:pumpkin_stem", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:sweet_berry_bush", 2d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:kelp", 2.5d, 1d, 15, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:bamboo", 2.5d, 2.5d, 10, 1.5d)
	));

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type plantGrowthModifierListType = new TypeToken<ArrayList<PlantGrowthModifier>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File plantGrowthModifiersFile = new File(jsonConfigFolder, "plants_growth_modifiers.json");
		if (!plantGrowthModifiersFile.exists()) {
			try {
				if (!plantGrowthModifiersFile.createNewFile()) {
					throw new Exception("File#createNewFile failed");
				}
				String json = gson.toJson(plantsList, plantGrowthModifierListType);
				Files.write(plantGrowthModifiersFile.toPath(), json.getBytes());
			}
			catch (Exception e) {
				LogHelper.error("Failed to create default Json %s: %s", FilenameUtils.removeExtension(plantGrowthModifiersFile.getName()), e.getMessage());
			}
		}

		plantsList.clear();
		try {
			FileReader fileReader = new FileReader(plantGrowthModifiersFile);
			List<PlantGrowthModifier> plantGrowthModifiers = gson.fromJson(fileReader, plantGrowthModifierListType);
			plantsList.addAll(plantGrowthModifiers);
		}
		catch (JsonSyntaxException e) {
			LogHelper.error("Parsing error loading Json %s: %s", FilenameUtils.removeExtension(plantGrowthModifiersFile.getName()), e.getMessage());
		}
		catch (Exception e) {
			LogHelper.error("Failed loading Json %s: %s", FilenameUtils.removeExtension(plantGrowthModifiersFile.getName()), e.getMessage());
		}
	}

	@SubscribeEvent
	public void cropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| plantsList.isEmpty())
			return;
		double multiplier = 1d;
		for (PlantGrowthModifier plantGrowthModifier : plantsList) {
			multiplier = plantGrowthModifier.getMultiplier(event.getState().getBlock(), (Level) event.getLevel(), event.getPos());
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
		if (event.getLevel().getRandom().nextDouble() > chance)
			event.setResult(Event.Result.DENY);
	}
}