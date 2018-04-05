package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.integration.BetterWithMods;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class ModuleHardness {
	public static void ProcessHardness(BreakSpeed event) {
		ProcessGlobalHardness(event);
		ProcessSingleHardness(event);
	}
	
	public static void ProcessGlobalHardness(BreakSpeed event) {
		if (!Properties.Global.hardness)
			return;
		
		if (Properties.Hardness.multiplier == 1.0f)
			return;
		
		World world = event.getEntityPlayer().world;
		
		IBlockState state = world.getBlockState(event.getPos());
		if (BetterWithMods.IsStumpOrRoot(state, world, event.getPos()))
			return;
		
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		boolean shouldProcess = true;
		for (String line : Properties.Hardness.blockHardness) {
			try {
				String block = line.split(",")[0];
				String hardness = line.split(",")[1];
				String modId = block.split(":")[0];
				String blockId = block.split(":")[1];
				ResourceLocation resourceLocation = new ResourceLocation(modId + ":" + blockId);
				
				int metadata = -1;
				if (block.split(":").length > 2) {
					metadata = Integer.parseInt(block.split(":")[2]);
					if (blockResource.equals(resourceLocation) && state.getBlock().getMetaFromState(state) == metadata) {
						shouldProcess = false;
						break;
					}
				}
				else {
					if (blockResource.equals(resourceLocation)) {
						shouldProcess = false;
						break;
					}
				}
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[block_hardness] Failed to parse line " + line + ": " + e);
			}
		}
		
		if (!shouldProcess)
			return;
		
		for (String line : Properties.Hardness.blockList) {
			//If is in blacklist mode
			if (!Properties.Hardness.blockListIsWhitelist){
				String block = line.split(":")[0] + ":" + line.split(":")[1];
				if (line.split(":").length == 3) {
					int meta = Integer.parseInt(line.split(":")[2]);
					if (block.equals(blockResource.toString()) && state.getBlock().getMetaFromState(state) == meta) {
						shouldProcess = false;
						break;
					}
				}
				else {
					if (block.equals(blockResource.toString())) {
						shouldProcess = false;
						break;
					}
				}
			}
			//If is in whitelist mode
			else {
				shouldProcess = false;
				String block = line.split(":")[0] + ":" + line.split(":")[1];
				if (line.split(":").length == 3) {
					int meta = Integer.parseInt(line.split(":")[2]);
					if (block.equals(blockResource.toString()) && state.getBlock().getMetaFromState(state) == meta) {
						shouldProcess = true;
						break;
					}
				}
				else {
					if (block.equals(blockResource.toString())) {
						shouldProcess = true;
						break;
					}
				}
			}
		}
		
		if (shouldProcess)
			event.setNewSpeed(event.getOriginalSpeed() / Properties.Hardness.multiplier);
	}
	
	public static void ProcessSingleHardness(BreakSpeed event) {
		if (!Properties.Global.hardness)
			return;
		
		if (Properties.Hardness.blockHardness.size() == 0)
			return;
		
		World world = event.getEntityPlayer().world;
		BlockPos pos = event.getPos();
		
		IBlockState state = event.getEntityPlayer().world.getBlockState(event.getPos());
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		for (String line : Properties.Hardness.blockHardness) { 
			try {
				String block = line.split(",")[0];
				String hardness = line.split(",")[1];
				
				String modId = block.split(":")[0];
				String blockId = block.split(":")[1];
				ResourceLocation resourceLocation = new ResourceLocation(modId + ":" + blockId);
				int metadata = -1;
				if (block.split(":").length > 2) {
					metadata = Integer.parseInt(block.split(":")[2]);
					if (blockResource.equals(resourceLocation) && state.getBlock().getMetaFromState(state) == metadata) {
						event.setNewSpeed(event.getOriginalSpeed() * GetRatio(hardness, blockId, state, world, pos));
						break;
					}
				}
				else {
					if (blockResource.equals(resourceLocation)) {
						event.setNewSpeed(event.getOriginalSpeed() * GetRatio(hardness, blockId, state, world, pos));
						break;
					}
				}
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[block_hardness] Failed to parse line " + line + ": " + e);
			}
		}
	}
	
	private static float GetRatio(String blockHardness, String blockId, IBlockState state, World world, BlockPos pos) {
		float hardness = Float.parseFloat(blockHardness);
		float originalHardness = Block.getBlockFromName(blockId).getBlockHardness(state, world, pos);
		float ratio = originalHardness / hardness;
		return ratio;
	}
}
