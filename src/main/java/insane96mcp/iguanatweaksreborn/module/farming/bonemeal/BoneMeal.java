package insane96mcp.iguanatweaksreborn.module.farming.bonemeal;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.criterion.ITRTriggers;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Label(name = "Bone meal", description = "Bone meal is no longer so OP and also Rich Farmland")
@LoadFeature(module = Modules.Ids.FARMING)
public class BoneMeal extends JsonFeature {

	public static final SimpleBlockWithItem RICH_FARMLAND = SimpleBlockWithItem.register("rich_farmland", () -> new RichFarmlandBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).randomTicks().strength(0.6F).sound(SoundType.GRAVEL).isViewBlocking((state, blockGetter, pos) -> true).isSuffocating((state, blockGetter, pos) -> true)));

	public static final TagKey<Item> ITEM_BLACKLIST = ITRItemTagsProvider.create("nerfed_bone_meal_blacklist");
	public static final TagKey<Block> BLOCK_BLACKLIST = ITRBlockTagsProvider.create("nerfed_bone_meal_blacklist");
	@Config
	@Label(name = "Nerfed Bone Meal", description = "Makes more Bone Meal required for Crops. Valid Values are\nNO: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
	public static BoneMealNerf nerfedBoneMeal = BoneMealNerf.NERFED;
	@Config
	@Label(name = "Transform Farmland in Rich Farmland", description = "Bone meal used on Farmland transforms it into Rich Farmland.")
	public static Boolean farmlandToRich = true;
	@Config(min = 1)
	@Label(name = "Rich Farmland Extra Ticks", description = "How many extra random ticks does Rich Farmland give to the crop sitting on top?")
	public static Integer richFarmlandExtraTicks = 3;

	@Config(min = 0d, max = 1d)
	@Label(name = "Rich Farmland Chance to Decay", description = "Chance for a Rich farmland to decay back to farmland")
	public static Double richFarmlandChanceToDecay = 0.4d;

	@Config
	@Label(name = "Bone meal dirt to grass", description = "If true, you can bone meal dirt that's near a grass block to get grass block.")
	public static Boolean boneMealDirtToGrass = true;

	@Config
	@Label(name = "Bone meal canes and cactus")
	public static Boolean boneMealCanesAndCactus = true;

	@Config
	@Label(name = "Compostable Rotten Flesh")
	public static Boolean compostableRottenFlesh = true;


	public static final List<IdTagValue> BONE_MEAL_FAIL_CHANCE_DEFAULT = new ArrayList<>(List.of(
			IdTagValue.newTag("minecraft:cave_vines", 0.75f),
			IdTagValue.newTag("minecraft:saplings", 0.2f)
	));
	public static final ArrayList<IdTagValue> boneMealFailChances = new ArrayList<>();

	public BoneMeal(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("fail_chances.json", boneMealFailChances, BONE_MEAL_FAIL_CHANCE_DEFAULT, IdTagValue.LIST_TYPE));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (compostableRottenFlesh)
			ComposterBlock.COMPOSTABLES.put(Items.ROTTEN_FLESH, 0.5f);
		else
			ComposterBlock.COMPOSTABLES.removeFloat(Items.ROTTEN_FLESH);
	}

	/**
	 * Handles part of crops require water too
	 */
	@SubscribeEvent
	public void onBonemeal(BonemealEvent event) {
		if (event.isCanceled()
				|| event.getResult() == Event.Result.DENY
				|| event.isCanceled()
				|| !this.isEnabled()
				|| event.getLevel().isClientSide)
			return;
		if (farmlandToRich){
			BlockPos farmlandPos = null;
			if (event.getBlock().is(Blocks.FARMLAND))
				farmlandPos = event.getPos();
			else if (event.getLevel().getBlockState(event.getPos().below()).is(Blocks.FARMLAND) && event.getEntity().isCrouching())
				farmlandPos = event.getPos().below();
			if (farmlandPos != null) {
				event.getLevel().setBlockAndUpdate(farmlandPos, RICH_FARMLAND.block().get().defaultBlockState().setValue(FarmBlock.MOISTURE, event.getLevel().getBlockState(farmlandPos).getValue(FarmBlock.MOISTURE)));
				event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
				event.setResult(Event.Result.ALLOW);
				ITRTriggers.MAKE_RICH_FARMLAND.trigger((ServerPlayer) event.getEntity());
			}
		}
		if (event.getResult() != Event.Result.ALLOW) {
			BoneMealResult result = applyBoneMeal(event.getLevel(), event.getStack(), event.getBlock(), event.getPos());
			if (result == BoneMealResult.ALLOW)
				event.setResult(Event.Result.ALLOW);
			else if (result == BoneMealResult.CANCEL)
				event.setCanceled(true);
		}

		if (event.getResult() != Event.Result.ALLOW) {
			if (boneMealDirtToGrass)
				tryBoneMealDirt(event, event.getLevel(), event.getBlock(), event.getPos());
			if (boneMealCanesAndCactus)
				tryBoneMealCanesAndCactus(event, event.getLevel(), event.getBlock(), event.getPos());
		}
	}

	private void tryBoneMealDirt(BonemealEvent event, Level level, BlockState state, BlockPos pos) {
		if (!state.is(Blocks.DIRT)
				|| !level.getBlockState(pos.above()).isAir())
			return;

		for (Direction direction : Direction.values()) {
			if (direction == Direction.UP || direction == Direction.DOWN)
				continue;

			if (level.getBlockState(pos.relative(direction)).is(Blocks.GRASS_BLOCK)) {
				level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
				event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
				event.setResult(Event.Result.ALLOW);
				break;
			}
		}
	}

	private void tryBoneMealCanesAndCactus(BonemealEvent event, Level level, BlockState state, BlockPos pos) {
		if (!state.is(Blocks.SUGAR_CANE) && !state.is(Blocks.CACTUS))
			return;
		if (!level.isEmptyBlock(pos.above()))
			return;
		IntegerProperty ageProperty = state.is(Blocks.SUGAR_CANE) ? SugarCaneBlock.AGE : CactusBlock.AGE;
		int height = 1;
		while (level.getBlockState(pos.below(height)).is(Blocks.SUGAR_CANE) || level.getBlockState(pos.below(height)).is(Blocks.CACTUS)) {
			height++;
		}
		if (height >= 3)
			return;

		int age = state.getValue(ageProperty);
		if (age == 15) {
			level.setBlockAndUpdate(pos.above(), state.getBlock().defaultBlockState());
			level.setBlock(pos, state.setValue(ageProperty, 0), 4);
		}
		else {
			level.setBlock(pos, state.setValue(ageProperty, Math.min(age + level.getRandom().nextInt(3) + 1, 15)), 4);
		}
		event.getEntity().swing(event.getEntity().getMainHandItem().getItem() == event.getStack().getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
		event.setResult(Event.Result.ALLOW);
	}

	public enum BoneMealResult {
		NONE,
		//Prevent using bone meal
		CANCEL,
		//Waste Bone meal
		ALLOW
	}

	public boolean shouldFail(BlockState state, RandomSource random) {
		for (IdTagValue boneMealFail : boneMealFailChances) {
			if (boneMealFail.id.matchesBlock(state.getBlock()) && random.nextFloat() < boneMealFail.value)
				return true;
		}
		return false;
	}

	public BoneMealResult applyBoneMeal(Level level, ItemStack stack, BlockState state, BlockPos pos) {
		if (stack.is(ITEM_BLACKLIST) || state.is(BLOCK_BLACKLIST))
			return BoneMealResult.NONE;

		//If farmland is dry and cropsRequireWater is enabled then cancel the event
		if (Crops.requiresWetFarmland(level, pos) && !Crops.hasWetFarmland(level, pos)) {
			return BoneMealResult.CANCEL;
		}
		if (shouldFail(state, level.random))
			return BoneMealResult.ALLOW;

		if (nerfedBoneMeal.equals(BoneMealNerf.NO))
			return BoneMealResult.NONE;
		if (state.getBlock() instanceof CropBlock cropBlock) {
			int age = state.getValue(cropBlock.getAgeProperty());
			int maxAge = Collections.max(cropBlock.getAgeProperty().getPossibleValues());
			if (age == maxAge) {
				return BoneMealResult.NONE;
			}

			if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT))
				age += Mth.nextInt(level.getRandom(), 1, 2);
			else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED))
				age++;
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(cropBlock.getAgeProperty(), age);
		}
		else if (state.getBlock() instanceof StemBlock stemBlock) {
			int age = state.getValue(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getPossibleValues());
			if (age == maxAge) {
				return BoneMealResult.NONE;
			}

			if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT))
				age += Mth.nextInt(level.getRandom(), 1, 2);
			else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED))
				age++;
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(StemBlock.AGE, age);
		}
		else
			return BoneMealResult.NONE;
		level.setBlockAndUpdate(pos, state);
		return BoneMealResult.ALLOW;
	}

	public enum BoneMealNerf {
		NO,
		SLIGHT,
		NERFED
	}
}
