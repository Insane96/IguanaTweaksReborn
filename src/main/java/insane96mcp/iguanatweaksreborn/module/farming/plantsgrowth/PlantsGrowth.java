package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
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

@Label(name = "Plants Growth", description = "Slower Plants (non-crops) growing. Plants properties are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.FARMING)
public class PlantsGrowth extends JsonFeature {

	public static final TagKey<Item> NO_FERTILITY_TOOLTIP = ITRItemTagsProvider.create("no_fertility_tooltip");

	/*public static final List<PlantGrowthMultiplier> PLANTS_LIST_DEFAULT = List.of(
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:sugar_cane")
					.setNoSunglightMultipler(1.75f, 10)build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:cactus")
					.setNoSunglightMultipler(1.5f, 10)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:cocoa")
					.setGrowthMultiplier(3f)
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_hot")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:nether_wart")
					.setGrowthMultiplier(2f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_nether")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:chorus_flower")
					.setGrowthMultiplier(2f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_end")
					)), 0f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.TAG, "minecraft:saplings")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:melon_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:pumpkin_stem")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:sweet_berry_bush")
					.setGrowthMultiplier(2f)
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.5f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_taiga")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:kelp")
					.setGrowthMultiplier(2f)
					.setNightTimeMultiplier(1.25f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_ocean"),
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_river")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:bamboo")
					.setNoSunglightMultipler(2.5f, 10)
					.setNightTimeMultiplier(1.25f)
					.setGrowthBiomes(new ArrayList<>(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "minecraft:is_hot")
					)), 3f).build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:wheat")
					.setNoSunglightMultipler(2f, 10)
					.setGrowthBiomes(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
					2f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:potatoes")
					.setNoSunglightMultipler(2f, 10)
					.setGrowthBiomes(List.of(
							new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
					2f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:beetroots")
					.setNoSunglightMultipler(2f, 10)
					.setGrowthBiomes(List.of(
									new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
							1.5f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:carrots")
					.setNoSunglightMultipler(2f, 10)
					.setGrowthBiomes(List.of(
									new IdTagMatcher(IdTagMatcher.Type.TAG, "forge:is_cold")),
							1.5f)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:torchflower_crop")
					.setNoSunglightMultipler(2f, 10)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "minecraft:pitcher_crop")
					.setNoSunglightMultipler(2f, 10)
					.inverseCorrectBiomes().build(),
			new PlantGrowthMultiplier.Builder(IdTagMatcher.Type.ID, "supplementaries:flax")
					.setNoSunglightMultipler(2f, 10)
					.inverseCorrectBiomes().build()
	);*/

	@Config
	@Label(name = "Huge mushrooms on Mycelium only")
	public static Boolean hugeMushroomsOnMyceliumOnly = true;

	@Config
	@Label(name = "Plant growth mulitpliers data pack", description = "If true, a data pack is enabled that changes the growth of plants based off various factors, such as sunglight and biome")
	public static Boolean plantGrowthMultipliersDataPack = true;

	public PlantsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "plant_growth_modifiers", Component.literal("IguanaTweaks Reborn Plant Growth modifiers"), () -> super.isEnabled() && !DataPacks.disableAllDataPacks && plantGrowthMultipliersDataPack));
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
				|| PlantsGrowthReloadListener.GROWTH_MULTIPLIERS.isEmpty())
			return;
		double multiplier = 1d;
		for (PlantGrowthMultiplier plantGrowthMultiplier : PlantsGrowthReloadListener.GROWTH_MULTIPLIERS) {
			multiplier *= plantGrowthMultiplier.getMultiplier(event.getState(), (Level) event.getLevel(), event.getPos());
		}
		if (multiplier == 0d) {
			event.setResult(Event.Result.DENY);
			return;
		}
		if (multiplier == 1d)
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
		/*if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)
				|| event.getItemStack().is(NO_FERTILITY_TOOLTIP))
			return;
		for (PlantGrowthMultiplier plantGrowthMultiplier : plantsList) {
			if (plantGrowthMultiplier.matchesBlock(blockItem.getBlock()) && !plantGrowthMultiplier.seasonsMultipliers.isEmpty()) {
				List<Season> infertileSeasons = new ArrayList<>();
				event.getToolTip().add(Component.translatable("desc.sereneseasons.fertile_seasons").append(":"));
				for (PlantGrowthMultiplier.SeasonMultiplier seasonMultiplier : plantGrowthMultiplier.seasonsMultipliers) {
					if (seasonMultiplier.multiplier() == 0f)
						infertileSeasons.add((Season) seasonMultiplier.season());
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
		}*/
	}
}
