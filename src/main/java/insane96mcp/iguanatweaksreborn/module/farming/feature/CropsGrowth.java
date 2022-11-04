package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.utils.PlantGrowthModifier;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@Label(name = "Crops Growth", description = "Slower Crops growing based off various factors")
@LoadFeature(module = Modules.Ids.FARMING)
public class CropsGrowth extends Feature {
	public static final ResourceLocation NO_GROWTH_MULTIPLIERS = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_growth_multipliers");

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

	public ArrayList<PlantGrowthModifier> plantGrowthModifiers = new ArrayList<>();

	public CropsGrowth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		if (plantGrowthModifiers.isEmpty()) {
			for (Block block : ForgeRegistries.BLOCKS.getValues()) {
				if (!(block instanceof CropBlock))
					continue;
				PlantGrowthModifier plantGrowthModifier = new PlantGrowthModifier(IdTagMatcher.Type.ID, ForgeRegistries.BLOCKS.getKey(block))
						.growthMultiplier(cropsGrowthMultiplier)
						.noSunlightGrowthMultiplier(noSunLightGrowthMultiplier)
						.minSunlightRequired(minSunlight)
						.nightTimeGrowthMultiplier(nightTimeGrowthMultiplier);
				plantGrowthModifiers.add(plantGrowthModifier);
			}
		}
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| cropsRequireWater.equals(CropsRequireWater.NO)
				|| event.getResult().equals(Event.Result.DENY)
				//|| this.cropsRequireWaterBlacklist.isBlockBlackOrNotWhiteListed(event.getState().getBlock())
				|| !isAffectedByFarmland(event.getLevel(), event.getPos()))
			return;
		// Denies the growth if the crop is on farmland and the farmland is wet. If it's not on farmland the growth is not denied (e.g. Farmer's Delight rice)
		if (isCropOnFarmland(event.getLevel(), event.getPos()) && !isCropOnWetFarmland(event.getLevel(), event.getPos())) {
			event.setResult(Event.Result.DENY);
		}
	}

	public static boolean requiresWetFarmland(Level level, BlockPos blockPos) {
		return isEnabled(CropsGrowth.class)
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
				|| isCropBlacklisted(event.getState().getBlock()))
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
		BONEMEAL_ONLY,
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

	public static boolean isCropBlacklisted(Block block) {
		TagKey<Block> tagKey = TagKey.create(Registry.BLOCK_REGISTRY, NO_GROWTH_MULTIPLIERS);
		return ForgeRegistries.BLOCKS.tags().getTag(tagKey).contains(block);
	}
}