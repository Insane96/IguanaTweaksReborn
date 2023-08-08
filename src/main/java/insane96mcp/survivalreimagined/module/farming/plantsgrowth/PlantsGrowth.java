package insane96mcp.survivalreimagined.module.farming.plantsgrowth;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.season.Season;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing. Plants properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends SRFeature {

	public static final ArrayList<PlantGrowthModifier> PLANTS_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:sugar_cane")
					.setNoSunglightMultipler(1.75f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:cactus")
					.setNoSunglightMultipler(1.5f, 10)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:cocoa")
					.setGrowthMultiplier(2f)
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:nether_wart")
					.setGrowthMultiplier(2f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_nether")
					)), 3f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:chorus_flower")
					.setGrowthMultiplier(2f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_end")
					)), 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.TAG, "minecraft:saplings")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.addSeasonMultiplier(Season.WINTER, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:melon_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:pumpkin_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.SUMMER, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:sweet_berry_bush")
					.setGrowthMultiplier(2f)
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_taiga")
					)), 3f)
					.addSeasonMultiplier(Season.WINTER, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:kelp")
					.setGrowthMultiplier(2f)
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
					)), 3f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f).build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:wheat")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.setGrowthBiomes(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
					2f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:potatoes")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f)
					.addSeasonMultiplier(Season.SUMMER, 0f)
					.setGrowthBiomes(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
					2f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:beetroots")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SPRING, 0f)
					.addSeasonMultiplier(Season.SUMMER, 0f)
					.setGrowthBiomes(List.of(
									new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
							1.5f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:carrots")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.SUMMER, 0f)
					.setGrowthBiomes(List.of(
									new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
							1.5f)
					.inverseCorrectBiomes().build()
	));
	public static final ArrayList<PlantGrowthModifier> plantsList = new ArrayList<>();

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("plants_growth_modifiers.json", plantsList, PLANTS_LIST_DEFAULT, PlantGrowthModifier.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.PLANTS_GROWTH));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	public static void handlePlantsGrowthPacket(String json) {
		loadAndReadJson(json, plantsList, PLANTS_LIST_DEFAULT, PlantGrowthModifier.LIST_TYPE);
	}

	@SubscribeEvent
	public void onCropGrowEvent(BlockEvent.CropGrowEvent.Pre event) {
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

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if (!(event.getItemStack().getItem() instanceof BlockItem blockItem))
			return;
		for (PlantGrowthModifier plantGrowthModifier : plantsList) {
			if (plantGrowthModifier.matchesBlock(blockItem.getBlock())) {
				List<Season> infertileSeasons = new ArrayList<>();
				event.getToolTip().add(Component.translatable("desc.sereneseasons.fertile_seasons").append(":"));
				for (PlantGrowthModifier.SeasonMultiplier seasonMultiplier : plantGrowthModifier.seasonsMultipliers) {
					if (seasonMultiplier.multiplier() == 0f)
						infertileSeasons.add(seasonMultiplier.season());
				}
				if (infertileSeasons.isEmpty())
					event.getToolTip().add(CommonComponents.space().append(Component.translatable("desc.sereneseasons.year_round")).withStyle(ChatFormatting.LIGHT_PURPLE));
				else {
					if (!infertileSeasons.contains(Season.SPRING))
						event.getToolTip().add(CommonComponents.space().append(Component.translatable("desc.sereneseasons.spring")).withStyle(ChatFormatting.GREEN));
					if (!infertileSeasons.contains(Season.SUMMER))
						event.getToolTip().add(CommonComponents.space().append(Component.translatable("desc.sereneseasons.summer")).withStyle(ChatFormatting.YELLOW));
					if (!infertileSeasons.contains(Season.AUTUMN))
						event.getToolTip().add(CommonComponents.space().append(Component.translatable("desc.sereneseasons.autumn")).withStyle(ChatFormatting.GOLD));
					if (!infertileSeasons.contains(Season.WINTER))
						event.getToolTip().add(CommonComponents.space().append(Component.translatable("desc.sereneseasons.winter")).withStyle(ChatFormatting.AQUA));
				}
			}
		}
	}
}
