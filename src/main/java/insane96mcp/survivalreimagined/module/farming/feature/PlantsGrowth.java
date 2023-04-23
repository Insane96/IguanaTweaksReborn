package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.utils.PlantGrowthModifier;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing. Plants properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends SRFeature {

	public static final ArrayList<PlantGrowthModifier> PLANTS_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:sugar_cane")
					.setNoSunglightMultipler(1.75f, 10).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:cactus")
					.setNoSunglightMultipler(1.5f, 10)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:cocoa")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:nether_wart")
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_nether")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:chorus_flower")
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_end")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.TAG, "minecraft:saplings")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:melon_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:pumpkin_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:sweet_berry_bush")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_taiga")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:kelp")
					.setGrowthMultiplier(1.25f)
					.setNightTimeMultiplier(1.25f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_ocean"),
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_river")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:bamboo")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.25f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_hot")
					)), 3f).build()
	));
	public static final ArrayList<PlantGrowthModifier> plantsList = new ArrayList<>();

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("plants_growth_modifiers.json", plantsList, PLANTS_LIST_DEFAULT, PlantGrowthModifier.LIST_TYPE));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
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