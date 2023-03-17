package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.utils.PlantGrowthModifier;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.config.FertilityConfig;

import java.util.ArrayList;

@Label(name = "Crops", description = "Slower Crops growing based off various factors and less yield from crops")
@LoadFeature(module = Modules.Ids.FARMING)
public class Crops extends Feature {
	public static final ResourceLocation NO_GROWTH_MULTIPLIERS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_growth_multipliers");

	@Config
	@Label(name = "Crops Require Water", description = """
						Set if crops require wet farmland to grow.
						Valid Values:
						NO: Crops will not require water to grow
						BONEMEAL_ONLY: Crops will grow on dry farmland by only using bonemeal
						ANY_CASE: Will make Crops not grow in any case when on dry farmland""")
	public static CropsRequireWater cropsRequireWater = CropsRequireWater.ANY_CASE;
	@Config(min = 0d, max = 128d)
	@Label(name = "Crops Growth Speed Multiplier", description = """
						Increases the time required for a crop (stems NOT included) to grow (e.g. at 2.0 the crop will take twice to grow).
						Setting this to 0 will prevent crops from growing naturally.
						1.0 will make crops grow like normal.""")
	public static Double cropsGrowthMultiplier = 2.5d;
	@Config(min = 0d, max = 128d)
	@Label(name = "No Sunlight Growth Multiplier", description = """
						Increases the time required for a crop to grow when it's sky light level is below "Min Sunlight", (e.g. at 2.0 when the crop has a skylight below "Min Sunlight" will take twice to grow).
						Setting this to 0 will prevent crops from growing when sky light level is below "Min Sunlight".
						1.0 will make crops growth not affected by skylight.""")
	public static Double noSunLightGrowthMultiplier = 2.0d;
	@Config(min = 0d, max = 128d)
	@Label(name = "Night Time Growth Multiplier", description = """
						Increases the time required for a crop to grow when it's night time.
						Setting this to 0 will prevent crops from growing when it's night time.
						1.0 will make crops growth not affected by night.""")
	public static Double nightTimeGrowthMultiplier = 1d;
	@Config(min = 0, max = 15)
	@Label(name = "Min Sunlight", description = "Minimum Sky Light level required for crops to not be affected by \"No Sunlight Growth Multiplier\".")
	public static Integer minSunlight = 10;

	@Config
	@Label(name = "Serene Seasons changes", description = """
			Makes the following changes to Serene Seasons config:
			* out_of_season_crop_behavior is set to 2 so out of season crops break when trying to grow
			* underground_fertility_level is set to -1
			""")
	public static Boolean changeSereneSeasonsConfig = true;

	public ArrayList<PlantGrowthModifier> plantGrowthModifiers = new ArrayList<>();

	public Crops(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		//Load crops in the list
		if (plantGrowthModifiers.isEmpty()) {
			for (Block block : ForgeRegistries.BLOCKS.getValues()) {
				if (!(block instanceof CropBlock))
					continue;
				//noinspection ConstantConditions
				PlantGrowthModifier plantGrowthModifier = new PlantGrowthModifier(IdTagMatcher.Type.ID, ForgeRegistries.BLOCKS.getKey(block).toString(), cropsGrowthMultiplier, noSunLightGrowthMultiplier, minSunlight, nightTimeGrowthMultiplier);
				plantGrowthModifiers.add(plantGrowthModifier);
			}
		}

		if (changeSereneSeasonsConfig) {
			FertilityConfig.outOfSeasonCropBehavior.set(2);
			FertilityConfig.undergroundFertilityLevel.set(-1);
		}
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| cropsRequireWater.equals(CropsRequireWater.NO)
				|| event.getResult().equals(Event.Result.DENY)
				|| !isAffectedByFarmland(event.getLevel(), event.getPos()))
			return;
		// Denies the growth if the crop is on farmland and the farmland is wet. If it's not on farmland the growth is not denied (e.g. Farmer's Delight rice)
		if (isCropOnFarmland(event.getLevel(), event.getPos()) && !isCropOnWetFarmland(event.getLevel(), event.getPos())) {
			event.setResult(Event.Result.DENY);
		}
	}

	public static boolean requiresWetFarmland(Level level, BlockPos blockPos) {
		return isEnabled(Crops.class)
				&& !cropsRequireWater.equals(CropsRequireWater.NO)
				&& isAffectedByFarmland(level, blockPos);
	}

	public static boolean hasWetFarmland(Level level, BlockPos blockPos) {
		return isCropOnFarmland(level, blockPos) && isCropOnWetFarmland(level, blockPos);
	}

	/**
	 * Handles Crop Growth Speed Multiplier, No Sunlight Growth multiplier and Night Time Growth Multiplier
	 */
	@SubscribeEvent
	public void cropsGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| event.getResult().equals(Event.Result.DENY)
				|| Utils.isBlockInTag(event.getState().getBlock(), NO_GROWTH_MULTIPLIERS))
			return;
		Level level = (Level) event.getLevel();
		double multiplier = 1d;
		for (PlantGrowthModifier plantGrowthModifier : plantGrowthModifiers) {
			multiplier = plantGrowthModifier.getMultiplier(event.getState().getBlock(), level, event.getPos());
			if (multiplier != -1d)
				break;
		}
		if (multiplier == 1d || multiplier == -1d)
			return;
		if (multiplier == 0d) {
			event.setResult(Event.Result.DENY);
			return;
		}
		double chance = 1d / multiplier;
		if (level.getRandom().nextDouble() > chance)
			event.setResult(Event.Result.DENY);
	}

	public enum CropsRequireWater {
		NO,
		BONE_MEAL_ONLY,
		ANY_CASE
	}

	/**
	 * @return true if the block is affected by the block below
	 */
	public static boolean isAffectedByFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState state = levelAccessor.getBlockState(cropPos);
		Block block = state.getBlock();
		return block instanceof CropBlock || block instanceof StemBlock;
	}

	/**
	 * @return true if the block is on wet farmland
	 */
	public static boolean isCropOnWetFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		if (!(sustainState.getBlock() instanceof FarmBlock))
			return false;
		int moisture = sustainState.getValue(FarmBlock.MOISTURE);
		return moisture >= 7;
	}


	/**
	 * @return true if the block is on farmland
	 */
	public static boolean isCropOnFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		return sustainState.getBlock() instanceof FarmBlock;
	}

	private static final String path = "crops/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "harder_beetroot_farming", new DropMultiplierModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.BEETROOTS).build()},
				Items.BEETROOT_SEEDS,
				0.137f,
				1
		));
		provider.add(path + "harder_wheat_farming", new DropMultiplierModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.WHEAT).build()},
				Items.WHEAT_SEEDS,
				0.137f,
				1
		));
		provider.add(path + "harder_potato_farming", new DropMultiplierModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.POTATOES).build()},
				Items.POTATO,
				0.1f,
				1
		));
		provider.add(path + "harder_carrot_farming", new DropMultiplierModifier(
				new LootItemCondition[]{new LootItemBlockStatePropertyCondition.Builder(Blocks.CARROTS).build()},
				Items.CARROT,
				0.1f,
				1
		));
	}
}