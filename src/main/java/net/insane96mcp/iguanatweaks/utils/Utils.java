package net.insane96mcp.iguanatweaks.utils;

import java.util.UUID;

import javax.annotation.Nullable;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleHardness.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class Utils {

	public static final UUID movSpeedRestrictionUUID = new UUID(22, 0x10000);
	public static final UUID swimSpeedRestrictionUUID = new UUID(22, 0x10002);
	public static final UUID stunUUID = new UUID(22, 0x10001);
		
	@Deprecated
	public static float GetBlockWeight(Block block) {
		Material blockMaterial = block.getMaterial(block.getDefaultState());
    	
        if (blockMaterial == Material.IRON || blockMaterial == Material.ANVIL) 
        	return 1.5f;
        else if (blockMaterial == Material.ROCK) 
        	return 1f;
        else if (blockMaterial == Material.GRASS || blockMaterial == Material.GROUND 
        		|| blockMaterial == Material.SAND || blockMaterial == Material.SNOW 
        		|| blockMaterial == Material.WOOD || blockMaterial == Material.GLASS 
        		|| blockMaterial == Material.ICE || blockMaterial == Material.TNT) 
        	return 0.5f;
        else if (blockMaterial == Material.CLOTH) 
        	return 0.25f;
        else if (block.isOpaqueCube(block.getDefaultState())) 
        	return 1f / 16f;
        else 
        	return 1f / 64f;
		
	}
	
	/**
	 * Given a string will be parsed to the block id and the meta if possible. Returns null if errored
	 */
	@Nullable
	public static BlockMeta parseBlock(String line, String featureError) {
		//Split block in modId blockId and meta
		String block = line;
		String[] splitBlock = block.split(":");
		
		if (splitBlock.length < 2 || splitBlock.length > 3) {
			IguanaTweaks.logger.error("[" + featureError + "] Failed to parse block " + block + " of line: " + line);
			return null;
		}
		String modId = splitBlock[0];
		String blockId = splitBlock[1];
		ResourceLocation id = new ResourceLocation(modId, blockId);
		int meta = -1;
		if (splitBlock.length == 3) {
			try {
				meta = Integer.parseInt(splitBlock[2]);
			} catch (Exception e) {
				IguanaTweaks.logger.error("[" + featureError + "] Failed to parse metadata for block " + block + " of line: " + line);
				return null;
			}
			if (meta < -1 || meta > Short.MAX_VALUE) {
				IguanaTweaks.logger.error("[" + featureError + "] Invalid metadata for block " + block + " of line: " + line);
				return null;
			}
		}
		return new BlockMeta(id, meta);
	}
}
