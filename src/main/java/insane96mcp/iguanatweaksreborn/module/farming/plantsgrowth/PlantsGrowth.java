package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.SRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sereneseasons.api.season.Season;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing. Plants properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends JsonFeature {

	public static final TagKey<Item> NO_FERTILITY_TOOLTIP = SRItemTagsProvider.create("no_fertility_tooltip");

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
					.setGrowthMultiplier(3f)
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
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:torchflower_crop")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.addSeasonMultiplier(Season.AUTUMN, 0f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "minecraft:pitcher_crop")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthModifier.Builder(IdTagMatcher.Type.ID, "supplementaries:flax")
					.setNoSunglightMultipler(2f, 10)
					.addSeasonMultiplier(Season.SUMMER, 0f)
					.addSeasonMultiplier(Season.WINTER, 0f)
					.inverseCorrectBiomes().build()
	));
	public static final ArrayList<PlantGrowthModifier> plantsList = new ArrayList<>();

	@Config
	@Label(name = "Huge mushrooms on Mycelium only")
	public static Boolean hugeMushroomsOnMyceliumOnly = true;

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "item_durabilities"), new SyncType(json -> loadAndReadJson(json, plantsList, PLANTS_LIST_DEFAULT, PlantGrowthModifier.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("plants_growth_modifiers.json", plantsList, PLANTS_LIST_DEFAULT, PlantGrowthModifier.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "plants_growth_modifiers")));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
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

	@SubscribeEvent
	public void onMushroomGrow(SaplingGrowTreeEvent event) {
		if (!this.isEnabled()
				|| !hugeMushroomsOnMyceliumOnly
				|| event.getFeature() == null)
			return;

		if ((event.getFeature().is(TreeFeatures.HUGE_BROWN_MUSHROOM) || event.getFeature().is(TreeFeatures.HUGE_RED_MUSHROOM)) && !event.getLevel().getBlockState(event.getPos().below()).is(Blocks.MYCELIUM)) {
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent
	public void onMushroomGrow(BonemealEvent event) {
		if (!this.isEnabled()
				|| !hugeMushroomsOnMyceliumOnly
				|| (!event.getBlock().is(Blocks.BROWN_MUSHROOM) && !event.getBlock().is(Blocks.RED_MUSHROOM)))
			return;

		if (!event.getLevel().getBlockState(event.getPos().below()).is(Blocks.MYCELIUM)) {
			event.setCanceled(true);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)
				|| event.getItemStack().is(NO_FERTILITY_TOOLTIP))
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
