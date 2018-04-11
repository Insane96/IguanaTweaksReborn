package net.insane96mcp.iguanatweaks.lib;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Utils {

	public static final UUID movementRestrictionUUID = new UUID(22, 0x10000);
	public static final UUID stunUUID = new UUID(22, 0x10001);
	
	@SuppressWarnings("deprecation")
	public static float GetItemWeight(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int meta = itemStack.getMetadata();
		Block block = Block.getBlockFromItem(item);
		IBlockState state = block.getStateFromMeta(meta);
		
		for (String line : Properties.MovementRestriction.customWeight) {
			String[] lineSplit = line.split(",");
			if (lineSplit.length != 2)
				continue;
			
			String[] itemSplit = lineSplit[0].split(":");
			if (itemSplit.length < 2 || itemSplit.length > 3)
				continue;
			ResourceLocation blockId = new ResourceLocation(itemSplit[0], itemSplit[1]);
			
			int customMeta = -1;
			if (itemSplit.length == 3)
				customMeta = Integer.parseInt(itemSplit[2]);
			
			float weight = Float.parseFloat(lineSplit[1]);
			
			if (item.getRegistryName().equals(blockId) && (item.getMetadata(itemStack) == customMeta || customMeta == -1)) {
				return weight;
			}
		}

		
		Material blockMaterial = block.getMaterial(state);
    	
        if (blockMaterial == Material.IRON || blockMaterial == Material.ANVIL) 
        	return 1.5f * Properties.MovementRestriction.rockWeight;
        else if (blockMaterial == Material.ROCK) 
        	return 1f * Properties.MovementRestriction.rockWeight;
        else if (blockMaterial == Material.GRASS || blockMaterial == Material.GROUND 
        		|| blockMaterial == Material.SAND || blockMaterial == Material.SNOW 
        		|| blockMaterial == Material.WOOD || blockMaterial == Material.GLASS 
        		|| blockMaterial == Material.ICE || blockMaterial == Material.TNT) 
        	return 0.5f * Properties.MovementRestriction.rockWeight;
        else if (blockMaterial == Material.CLOTH) 
        	return 0.25f * Properties.MovementRestriction.rockWeight;
        else if (block.isOpaqueCube(state)) 
        	return 1f / 16f * Properties.MovementRestriction.rockWeight;
        else 
        	return 1f / 64f * Properties.MovementRestriction.rockWeight;
	}
	
	
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
}
