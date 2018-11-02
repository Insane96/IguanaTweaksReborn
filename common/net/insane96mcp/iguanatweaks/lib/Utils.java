package net.insane96mcp.iguanatweaks.lib;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Utils {

	public static final UUID movementRestrictionUUID = new UUID(22, 0x10000);
	public static final UUID stunUUID = new UUID(22, 0x10001);
	
	@SuppressWarnings("deprecation")
	public static float GetItemWeight(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int meta = itemStack.getMetadata();
		Block block = Block.getBlockFromItem(item);
		IBlockState state = block.getStateFromMeta(meta);
		
		for (String line : Properties.config.movementRestriction.customWeight) {
			String[] lineSplit = line.split(",");
			if (lineSplit.length != 2)
				continue;
			
			String[] itemSplit = lineSplit[0].split(":");
			if (itemSplit.length < 2 || itemSplit.length > 3)
				continue;
			ResourceLocation itemId = new ResourceLocation(itemSplit[0], itemSplit[1]);
			
			int customMeta = -1;
			if (itemSplit.length == 3)
				customMeta = Integer.parseInt(itemSplit[2]);
			
			float weight = Float.parseFloat(lineSplit[1]);
			
			if (item.getRegistryName().equals(itemId) && (meta == customMeta || customMeta == -1)) {
				return weight;
			}
		}

		return GetBlockWeight(block) * Properties.config.movementRestriction.rockWeight;
	}
	
	@SuppressWarnings("deprecation")
	public static float GetBlockSlowness(World world, BlockPos pos) {
		Material blockOnMaterial = world.getBlockState(pos).getMaterial();			
		Material blockInMaterial = world.getBlockState(pos.add(0, 1, 0)).getMaterial();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		
		float slowness = -1f;
		
		for (String line : Properties.config.movementRestriction.terrainSlowdownCustom) {
			String[] lineSplit = line.split(",");
			if (lineSplit.length != 2)
				continue;
			
			String[] blockSplit = lineSplit[0].split(":");
			if (blockSplit.length < 2 || blockSplit.length > 3)
				continue;
			ResourceLocation blockId = new ResourceLocation(blockSplit[0], blockSplit[1]);
			
			int customMeta = -1;
			if (blockSplit.length == 3)
				customMeta = Integer.parseInt(blockSplit[2]);
			
			if (block.getRegistryName().equals(blockId) && (meta == customMeta || customMeta == -1))
				slowness = Float.parseFloat(lineSplit[1]);
		}
		
		if (slowness == -1f) {
	        if (blockOnMaterial == Material.GRASS || blockOnMaterial == Material.GROUND) 
	        	slowness = Properties.config.movementRestriction.terrainSlowdownOnDirt; 
	        else if (blockOnMaterial == Material.SAND) 
	        	slowness = Properties.config.movementRestriction.terrainSlowdownOnSand;
	        else if (blockOnMaterial == Material.LEAVES || blockOnMaterial == Material.PLANTS || blockOnMaterial == Material.VINE) 
	        	slowness = Properties.config.movementRestriction.terrainSlowdownOnPlant;
	        else if (blockOnMaterial == Material.ICE || blockOnMaterial == Material.PACKED_ICE)
	        	slowness = Properties.config.movementRestriction.terrainSlowdownOnIce;
	        else if (blockOnMaterial == Material.SNOW || blockOnMaterial == Material.CRAFTED_SNOW)
	        	slowness = Properties.config.movementRestriction.terrainSlowdownOnSnow;
	        else
	        	slowness = 0;
		}
        if (blockInMaterial == Material.SNOW || blockInMaterial == Material.CRAFTED_SNOW) 
        	slowness += Properties.config.movementRestriction.terrainSlowdownInSnow;
		else if (blockInMaterial == Material.VINE || blockInMaterial == Material.PLANTS) 
			slowness += Properties.config.movementRestriction.terrainSlowdownInPlant;

        return slowness;
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
