package net.insane96mcp.iguanatweaks.modules;

import com.google.common.collect.ImmutableList;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class ModuleHardness {
	public static void ProcessGlobalHardness() {
		if (!Properties.Global.hardness)
			return;
		
		if (Properties.Hardness.multiplier == 1.0f)
			return;

		for (Block block : Block.REGISTRY) {
			ResourceLocation blockResource = block.getRegistryName();
			if ((Properties.Hardness.blockListIsWhitelist && Properties.Hardness.blockList.contains(blockResource.toString()))
				|| !Properties.Hardness.blockListIsWhitelist && !Properties.Hardness.blockList.contains(blockResource.toString())){
				float hardness = 0.0f;
				ImmutableList<IBlockState> blockStates = block.getBlockState().getValidStates();
				for (IBlockState state : blockStates) {
					try {
						hardness = block.getBlockHardness(state, null, null);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (hardness != 0.0f)
						break;
				}
				if (hardness != 0.0f)
					block.setHardness(hardness * Properties.Hardness.multiplier);
			}
		}
	}
	
	public static void ProcessSingleHardness() {
		if (!Properties.Global.hardness)
			return;
		
		if (Properties.Hardness.blockHardness.size() == 0)
			return;
		
		for (String line : Properties.Hardness.blockHardness) {
			try {
				ResourceLocation blockId = new ResourceLocation(line.split(",")[0]);
				float hardness = Float.parseFloat(line.split(",")[1]);
				
				Block.REGISTRY.getObject(blockId).setHardness(hardness);
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[block_hardness] Failed to parse line " + line + ": " + e.getMessage());
			}
		}
	}
}
