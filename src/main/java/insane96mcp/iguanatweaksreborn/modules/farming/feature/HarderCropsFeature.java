package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.block.Block;
import net.minecraft.block.CropsBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Harder Crops", description = "Crops are no longer insta-minable. This applies only to blocks that are instances of net.minecraft.world.level.block.CropBlock.\n" +
		"Crops hardness is still affected by the Hardness module.\n" +
		"Changing anything requires a minecraft restart.")
public class HarderCropsFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> hardnessConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> moreBlocksListConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> onlyFullyGrownConfig;

	public double hardness = 1.0f;
	public ArrayList<IdTagMatcher> moreBlocksList;
	public boolean onlyFullyGrown = true;

	public HarderCropsFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hardnessConfig = Config.builder
				.comment("How hard to break are plants? For comparison, dirt has an hardness of 0.5")
				.defineInRange("Hardness", hardness, 0.0d, 128d);
		moreBlocksListConfig = Config.builder
				.comment("Block ids or tags that will have the hardness and hoe efficiency applied. Each entry has a block or tag. This still only applies to blocks that have 0 hardness.")
				.defineList("Other affected blocks", new ArrayList<>(), o -> o instanceof String);
		onlyFullyGrownConfig = Config.builder
				.comment("If the hardness should be applied to mature crops only.")
				.define("Only fully grown", onlyFullyGrown);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hardness = this.hardnessConfig.get();
		this.moreBlocksList = IdTagMatcher.parseStringList(this.moreBlocksListConfig.get());
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
			if (!(block instanceof CropsBlock) && !isInWhitelist)
				continue;
			if (onlyFullyGrown) {
				//I have doubts that this always takes the fully grown crop
				block.getStateContainer().getValidStates().get(block.getStateContainer().getValidStates().size() - 1).hardness = (float) this.hardness;
			}
			else {
				block.getStateContainer().getValidStates().forEach(blockState -> {
					if (blockState.hardness == 0f)
						blockState.hardness = (float) this.hardness;
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
		ItemStack heldStack = event.getPlayer().getHeldItemMainhand();
		if (!(heldStack.getItem() instanceof TieredItem))
			return;
		TieredItem heldItem = (TieredItem) heldStack.getItem();
		if (!heldItem.getToolTypes(heldStack).contains(ToolType.HOE) && !heldItem.getToolTypes(heldStack).contains(ToolType.AXE))
			return;
		Block block = event.getState().getBlock();
		boolean isInWhitelist = false;
		for (IdTagMatcher blacklistEntry : this.moreBlocksList) {
			if (blacklistEntry.matchesBlock(block, null)) {
				isInWhitelist = true;
				break;
			}
		}
		if (!(block instanceof CropsBlock) && !isInWhitelist)
			return;
		float efficiency = heldItem.getTier().getEfficiency();
		if (efficiency > 1.0F) {
			int efficiencyLevel = EnchantmentHelper.getEfficiencyModifier(event.getPlayer());
			ItemStack itemstack = event.getPlayer().getHeldItemMainhand();
			if (efficiencyLevel > 0 && !itemstack.isEmpty()) {
				efficiency += (float) (efficiencyLevel * efficiencyLevel + 1);
			}
		}
		if (heldItem.getToolTypes(heldStack).contains(ToolType.HOE))
			event.setNewSpeed(event.getNewSpeed() * efficiency);
		else
			event.setNewSpeed(event.getNewSpeed() / efficiency);

	}
}
