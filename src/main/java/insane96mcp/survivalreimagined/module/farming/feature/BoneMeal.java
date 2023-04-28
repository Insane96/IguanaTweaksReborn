package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.block.RichFarmlandBlock;
import insane96mcp.survivalreimagined.setup.SRBlocks;
import insane96mcp.survivalreimagined.setup.SRItems;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;

@Label(name = "Bone meal", description = "Bone meal is no longer so OP and also Rich Farmland")
@LoadFeature(module = Modules.Ids.FARMING)
public class BoneMeal extends Feature {

	public static final RegistryObject<RichFarmlandBlock> RICH_FARMLAND = SRBlocks.REGISTRY.register("rich_farmland", () -> new RichFarmlandBlock(BlockBehaviour.Properties.of(Material.DIRT).randomTicks().strength(0.6F).sound(SoundType.GRAVEL).isViewBlocking((state, blockGetter, pos) -> true).isSuffocating((state, blockGetter, pos) -> true)));
	public static final RegistryObject<BlockItem> RICH_FARMLAND_ITEM = SRItems.REGISTRY.register("rich_farmland", () -> new BlockItem(RICH_FARMLAND.get(), new Item.Properties()));

	private static final ResourceLocation BLACKLIST = new ResourceLocation(SurvivalReimagined.MOD_ID, "nerfed_bone_meal_blacklist");
	@Config
	@Label(name = "Nerfed Bone Meal", description = "Makes more Bone Meal required for Crops. Valid Values are\nNO: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
	public static BoneMealNerf nerfedBoneMeal = BoneMealNerf.NERFED;
	@Config(min = 0d, max = 1d)
	@Label(name = "Bone Meal Fail Chance", description = "Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 1 to disable Bone Meal.")
	public static Double boneMealFailChance = 0d;

	@Config
	@Label(name = "Transform Farmland in Rich Farmland", description = "Bone meal used on Farmland transforms it into Rich Farmland.")
	public static Boolean farmlandToRich = true;
	@Config(min = 1)
	@Label(name = "Rich Farmland Extra Ticks", description = "How many extra random ticks does Rich Farmland give to the crop sitting on top?")
	public static Integer richFarmlandExtraTicks = 3;
	@Config(min = 0d, max = 1d)
	@Label(name = "Rich Farmland Chance to Decay", description = "Chance for a Rich farmland to decay back to farmland")
	public static Double richFarmlandChanceToDecay = 0.05d;

	public BoneMeal(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	/**
	 * Handles part of crops require water too
	 */
	@SubscribeEvent
	public void nerfBoneMeal(BonemealEvent event) {
		if (event.isCanceled()
				|| event.getResult() == Event.Result.DENY
				|| !this.isEnabled()
				|| event.getLevel().isClientSide)
			return;
		if (farmlandToRich && event.getBlock().is(Blocks.FARMLAND)){
			event.getLevel().setBlockAndUpdate(event.getPos(), RICH_FARMLAND.get().defaultBlockState().setValue(FarmBlock.MOISTURE, event.getBlock().getValue(FarmBlock.MOISTURE)));
			event.getEntity().swing(event.getEntity().getUsedItemHand(), true);
			event.setResult(Event.Result.ALLOW);
		}
		else {
			BoneMealResult result = applyBoneMeal(event.getLevel(), event.getStack(), event.getBlock(), event.getPos());
			if (result == BoneMealResult.ALLOW)
				event.setResult(Event.Result.ALLOW);
			else if (result == BoneMealResult.CANCEL)
				event.setCanceled(true);
		}
	}

	public enum BoneMealResult {
		NONE,
		CANCEL,
		ALLOW
	}

	public BoneMealResult applyBoneMeal(Level level, ItemStack stack, BlockState state, BlockPos pos) {
		if (Utils.isItemInTag(stack.getItem(), BLACKLIST) || Utils.isBlockInTag(state.getBlock(), BLACKLIST))
			return BoneMealResult.NONE;

		//If farmland is dry and cropsRequireWater is enabled then cancel the event
		if (Crops.requiresWetFarmland(level, pos) && !Crops.hasWetFarmland(level, pos)) {
			return BoneMealResult.CANCEL;
		}

		if (nerfedBoneMeal.equals(BoneMealNerf.NO))
			return BoneMealResult.NONE;
		if (state.getBlock() instanceof CropBlock cropBlock) {
			int age = state.getValue(cropBlock.getAgeProperty());
			int maxAge = Collections.max(cropBlock.getAgeProperty().getPossibleValues());
			if (age == maxAge) {
				return BoneMealResult.NONE;
			}

			if (level.getRandom().nextDouble() < boneMealFailChance) {
				return BoneMealResult.ALLOW;
			} else if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			} else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED)) {
				age++;
			}
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(cropBlock.getAgeProperty(), age);
		} else if (state.getBlock() instanceof StemBlock) {
			int age = state.getValue(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getPossibleValues());
			if (age == maxAge) {
				return BoneMealResult.NONE;
			}

			if (level.getRandom().nextDouble() < boneMealFailChance) {
				return BoneMealResult.ALLOW;
			} else if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			} else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED)) {
				age++;
			}
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(StemBlock.AGE, age);
		} else
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
