package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;

@Label(name = "Nerfed Bone meal", description = "Bone meal is no longer so OP")
@LoadFeature(module = Modules.Ids.FARMING)
public class NerfedBoneMeal extends Feature {
	private static final ResourceLocation BLACKLIST = new ResourceLocation(SurvivalReimagined.MOD_ID, "nerfed_bone_meal_blacklist");
	@Config
	@Label(name = "Nerfed Bone Meal", description = "Makes more Bone Meal required for Crops. Valid Values are\nNO: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
	public static BoneMealNerf nerfedBoneMeal = BoneMealNerf.NERFED;
	@Config(min = 0d, max = 1d)
	@Label(name = "Bone Meal Fail Chance", description = "Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 1 to disable Bone Meal.")
	public static Double boneMealFailChance = 0d;

	public NerfedBoneMeal(Module module, boolean enabledByDefault, boolean canBeDisabled) {
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
		BoneMealResult result = applyBoneMeal(event.getLevel(), event.getStack(), event.getBlock(), event.getPos());
		if (result == BoneMealResult.ALLOW)
			event.setResult(Event.Result.ALLOW);
		else if (result == BoneMealResult.CANCEL)
			event.setCanceled(true);
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
			}
			else if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			}
			else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED)) {
				age++;
			}
			age = Mth.clamp(age, 0, maxAge);
			state = state.setValue(cropBlock.getAgeProperty(), age);
		}
		else if (state.getBlock() instanceof StemBlock) {
			int age = state.getValue(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getPossibleValues());
			if (age == maxAge) {
				return BoneMealResult.NONE;
			}

			if (level.getRandom().nextDouble() < boneMealFailChance) {
				return BoneMealResult.ALLOW;
			}
			else if (nerfedBoneMeal.equals(BoneMealNerf.SLIGHT)) {
				age += Mth.nextInt(level.getRandom(), 1, 2);
			}
			else if (nerfedBoneMeal.equals(BoneMealNerf.NERFED)) {
				age++;
			}
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
