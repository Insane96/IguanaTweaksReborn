package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Harder Crops", description = "Crops are no longer insta-minable. This applies only to blocks that are instances of net.minecraft.world.level.block.CropBlock.\n" +
		"Crops hardness is still affected by the Hardness module.\n" +
		"Changing anything requires a minecraft restart.")
public class HarderCrops extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> hardnessConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> moreBlocksListConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> onlyFullyGrownConfig;

	public double hardness = 1.0f;
	public ArrayList<IdTagMatcher> moreBlocksList;
	public boolean onlyFullyGrown = true;

	public HarderCrops(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		hardnessConfig = ITCommonConfig.builder
				.comment("How hard to break are plants? For comparison, dirt has an hardness of 0.5")
				.defineInRange("Hardness", hardness, 0.0d, 128d);
		moreBlocksListConfig = ITCommonConfig.builder
				.comment("Block ids or tags that will have the hardness and hoe efficiency applied. Each entry has a block or tag. This still only applies to blocks that have 0 hardness.")
				.defineList("Other affected blocks", new ArrayList<>(), o -> o instanceof String);
		onlyFullyGrownConfig = ITCommonConfig.builder
				.comment("If the hardness should be applied to mature crops only.")
				.define("Only fully grown", onlyFullyGrown);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hardness = this.hardnessConfig.get();
		this.moreBlocksList = (ArrayList<IdTagMatcher>) IdTagMatcher.parseStringList(this.moreBlocksListConfig.get());
		this.onlyFullyGrown = this.onlyFullyGrownConfig.get();
		applyHardness();
	}

	private boolean hardnessApplied = false;

	private void applyHardness() {
		if (!this.isEnabled())
			return;
		if (hardnessApplied)
			return;
		hardnessApplied = true;

		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			boolean isInWhitelist = false;
			for (IdTagMatcher blacklistEntry : this.moreBlocksList) {
				if (blacklistEntry.matchesBlock(block, null)) {
					isInWhitelist = true;
					break;
				}
			}
			if (!(block instanceof CropBlock) && !isInWhitelist)
				continue;
			if (onlyFullyGrown) {
				//I have doubts that this always takes the fully grown crop
				block.getStateDefinition().getPossibleStates().get(block.getStateDefinition().getPossibleStates().size() - 1).destroySpeed = (float) this.hardness;
			}
			else {
				block.getStateDefinition().getPossibleStates().forEach(blockState -> {
					if (blockState.destroySpeed == 0f)
						blockState.destroySpeed = (float) this.hardness;
				});
			}
		}
	}

	@SubscribeEvent
	public void onCropBreaking(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;
		if (this.hardness == 0d)
			return;
		ItemStack heldStack = event.getPlayer().getMainHandItem();
		if (!(heldStack.getItem() instanceof TieredItem))
			return;
		TieredItem heldItem = (TieredItem) heldStack.getItem();
		if (!heldItem.canPerformAction(heldStack, ToolActions.HOE_DIG) && !heldItem.canPerformAction(heldStack, ToolActions.AXE_DIG))
			return;
		Block block = event.getState().getBlock();
		boolean isInWhitelist = false;
		for (IdTagMatcher blacklistEntry : this.moreBlocksList) {
			if (blacklistEntry.matchesBlock(block, null)) {
				isInWhitelist = true;
				break;
			}
		}
		if (!(block instanceof CropBlock) && !isInWhitelist)
			return;
		float efficiency = heldItem.getTier().getSpeed();
		if (efficiency > 1.0F) {
			int efficiencyLevel = EnchantmentHelper.getBlockEfficiency(event.getPlayer());
			ItemStack itemstack = event.getPlayer().getMainHandItem();
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