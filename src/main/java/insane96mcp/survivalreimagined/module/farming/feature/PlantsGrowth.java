package insane96mcp.survivalreimagined.module.farming.feature;

import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing. Plants properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends SRFeature {

	//TODO Add Wrong season multiplier
	//TODO Change SS tags and remove saplings from winter (maybe even something else)
	public static final ArrayList<PlantGrowthModifier> PLANTS_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:sugar_cane", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:cactus", 2.5d, 1.5d, 10, 1d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
			)), 3d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:cocoa", 3d, 2.5d, 10, 1.5d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
			)), 3d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:nether_wart", 3d, 1d, 0, 1d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_nether")
			)), 3d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:chorus_flower", 3d, 1d, 0, 1d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_end")
			)), 3d),
			new PlantGrowthModifier(IdTagMatcher.Type.TAG, "minecraft:saplings", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:melon_stem", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:pumpkin_stem", 2.5d, 2.5d, 10, 1.5d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:sweet_berry_bush", 2d, 2.5d, 10, 1.5d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_taiga")
			)), 3d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:kelp", 2.5d, 1d, 0, 1.5d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_ocean"),
					new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_river")
			)), 2d),
			new PlantGrowthModifier(IdTagMatcher.Type.ID, "minecraft:bamboo", 2.5d, 2.5d, 10, 1.5d, new ArrayList<>(List.of(
					new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
			)), 3d)
	));
	public static final ArrayList<PlantGrowthModifier> plantsList = new ArrayList<>();

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type plantGrowthModifierListType = new TypeToken<ArrayList<PlantGrowthModifier>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("plants_growth_modifiers.json", plantsList, PLANTS_LIST_DEFAULT, plantGrowthModifierListType);
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