package net.insane96mcp.iguanatweaks.lib;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class Utils {

	public static UUID movementRestrictionUUID = new UUID(22, 0x10000);
	public static UUID damageSlowdownUUID = new UUID(22, 0x10001);
	
	@SuppressWarnings("deprecation")
	public static float getBlockWeight(Block block) {
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
        	return 1f / 64f; // item like block
	}
}
