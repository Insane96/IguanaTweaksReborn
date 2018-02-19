package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class ModuleHardness {
	public static void ProcessGlobalHardness(BreakSpeed event) {
		if (!Properties.Global.hardness)
			return;
		
		if (Properties.Hardness.multiplier == 1.0f)
			return;
		
		ResourceLocation blockResource = event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock().getRegistryName();
		boolean shouldProcess = true;
		for (String line : Properties.Hardness.blockHardness) {
			try {
				ResourceLocation blockId = new ResourceLocation(line.split(",")[0]);
				if (blockResource.equals(blockId)) {
					shouldProcess = false;
					break;
				}
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[block_hardness] Failed to parse line " + line + ": " + e.getMessage());
			}
		}
		
		if (!shouldProcess)
			return;
		
		if ((Properties.Hardness.blockListIsWhitelist && Properties.Hardness.blockList.contains(blockResource.toString()))
				|| !Properties.Hardness.blockListIsWhitelist && !Properties.Hardness.blockList.contains(blockResource.toString()))
		{
			event.setNewSpeed(event.getOriginalSpeed() / Properties.Hardness.multiplier);
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
