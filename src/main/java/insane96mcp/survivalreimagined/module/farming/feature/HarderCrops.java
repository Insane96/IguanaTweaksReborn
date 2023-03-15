package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@Label(name = "Harder Crops", description = """
		Crops are no longer insta-minable. This applies only to blocks that are instances of net.minecraft.world.level.block.CropBlock.
		Break speed is still affected by the Hardness module.""")
@LoadFeature(module = Modules.Ids.FARMING)
public class HarderCrops extends SRFeature {
	public static final ResourceLocation HARDER_CROPS_TAG = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "harder_crops");
	public static final ResourceLocation HARDER_CROPS_BLACKLIST_TAG = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "harder_crops_blacklist");
	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness", description = "How hard to break are plants? For comparison, dirt has an hardness of 0.5")
	public static Double hardness = 1.0d;
	@Config
	@Label(name = "Only fully grown", description = "If the hardness should be applied to mature crops only.")
	public static Boolean onlyFullyGrown = true;

	public HarderCrops(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;

		resetOriginalHardness();
		applyHardness();
	}

	ArrayList<BlockState> changedStates = new ArrayList<>();

	private void resetOriginalHardness() {
		if (changedStates.isEmpty()) {
			for (Block block : ForgeRegistries.BLOCKS.getValues()) {
				boolean isInTag = isBlockInTag(block, HARDER_CROPS_TAG);
				if (!(block instanceof CropBlock) && !isInTag)
					continue;

				block.getStateDefinition().getPossibleStates().forEach(blockState -> {
					if (blockState.destroySpeed == 0f)
						changedStates.add(blockState);
				});
			}
		}
		else {
			for (BlockState state : changedStates) {
				state.destroySpeed = 0f;
			}
		}
	}

	private void applyHardness() {
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			boolean isInTag = isBlockInTag(block, HARDER_CROPS_TAG);
			boolean isBlacklisted = isBlockInTag(block, HARDER_CROPS_BLACKLIST_TAG);
			if (!(block instanceof CropBlock) && !isInTag)
				continue;
			if (isBlacklisted)
				continue;
			if (onlyFullyGrown) {
				//I have doubts that this always takes the fully grown modded crops
				BlockState state = block.getStateDefinition().getPossibleStates().get(block.getStateDefinition().getPossibleStates().size() - 1);
				if (state.destroySpeed == 0f)
					state.destroySpeed = hardness.floatValue();
			}
			else {
				block.getStateDefinition().getPossibleStates().forEach(blockState -> {
					if (blockState.destroySpeed == 0f)
						blockState.destroySpeed = hardness.floatValue();
				});
			}
		}
	}

	@SubscribeEvent
	public void onCropBreaking(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| hardness == 0d)
			return;
		ItemStack heldStack = event.getEntity().getMainHandItem();
		if (!(heldStack.getItem() instanceof TieredItem heldItem))
			return;
		if (!heldItem.canPerformAction(heldStack, ToolActions.HOE_DIG) && !heldItem.canPerformAction(heldStack, ToolActions.AXE_DIG))
			return;
		Block block = event.getState().getBlock();
		boolean isInTag = Utils.isBlockInTag(block, HARDER_CROPS_TAG);
		if (!(block instanceof CropBlock) && !isInTag)
			return;
		float efficiency = heldItem.getTier().getSpeed();
		if (efficiency > 1.0F) {
			int efficiencyLevel = EnchantmentHelper.getBlockEfficiency(event.getEntity());
			ItemStack itemstack = event.getEntity().getMainHandItem();
			if (efficiencyLevel > 0 && !itemstack.isEmpty()) {
				efficiency += (float) (efficiencyLevel * efficiencyLevel + 1);
			}
		}
		if (heldItem.canPerformAction(heldStack, ToolActions.HOE_DIG))
			event.setNewSpeed(event.getNewSpeed() * efficiency);
		else
			event.setNewSpeed(event.getNewSpeed() / efficiency);

	}
}