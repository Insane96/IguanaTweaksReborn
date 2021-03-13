package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
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

@Label(name = "Harder Crops", description = "Crops are no longer insta-minable")
public class HarderCrops extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> hardnessConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistConfig;

	public double hardness = 1.0f;
	public ArrayList<IdTagMatcher> whitelist;

	public HarderCrops(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hardnessConfig = Config.builder
				.comment("Makes crop no longer instantly break. Using an hoe will speed up the break process.\n" +
						"Tecnicality: this applies to any plant that is instance of net.minecraft.block.CropBlock that can be insta-mined (has 0 hardness)\n" +
						"Crops hardness is affected by the hardness module.")
				.defineInRange("Hardness", hardness, 0.0d, 128d);
		whitelistConfig = Config.builder
				.comment("Block ids or tags that will have the hardness and hoe efficiency applied. Each entry has a block or tag. This still only applies to blocks that have 0 hardness.")
				.defineList("Whitelist", new ArrayList<>(), o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hardness = this.hardnessConfig.get();
		this.whitelist = IdTagMatcher.parseStringList(this.whitelistConfig.get());
		applyHardness();
	}

	private final ArrayList<Block> affectedBlocks = new ArrayList<>();

	public void applyHardness() {
		if (!this.isEnabled())
			return;
		//Reset affected blocks to hardness 0f
		for (Block block : affectedBlocks) {
			block.getStateContainer().getValidStates().forEach(blockState -> {
				blockState.hardness = 0f;
			});
		}
		affectedBlocks.clear();
		for (Block block : ForgeRegistries.BLOCKS.getValues()) {
			boolean isInWhitelist = false;
			for (IdTagMatcher blacklistEntry : this.whitelist) {
				if (blacklistEntry.isInTagOrBlock(block, null)) {
					isInWhitelist = true;
					break;
				}
			}
			if (!(block instanceof CropsBlock) && !isInWhitelist)
				continue;
			block.getStateContainer().getValidStates().forEach(blockState -> {
				if (blockState.hardness == 0f) {
					blockState.hardness = (float) this.hardness;
				}
			});
			affectedBlocks.add(block);
		}
	}

	@SubscribeEvent
	public void processSingleHardness(PlayerEvent.BreakSpeed event) {
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
		for (IdTagMatcher blacklistEntry : this.whitelist) {
			if (blacklistEntry.isInTagOrBlock(block, null)) {
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
