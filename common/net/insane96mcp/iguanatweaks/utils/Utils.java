package net.insane96mcp.iguanatweaks.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Utils {
/*
	public static final UUID movSpeedRestrictionUUID = new UUID(22, 0x10000);
	public static final UUID swimSpeedRestrictionUUID = new UUID(22, 0x10002);
	public static final UUID stunUUID = new UUID(22, 0x10001);
	
	@SuppressWarnings("deprecation")
	public static float getItemWeight(ItemStack itemStack) {
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
	public static float getBlockSlowness(World world, BlockPos pos) {
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
	}*/
	
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
        /*else if (block.isOpaqueCube(block.getDefaultState())) 
        	return 1f / 16f;*/
        else 
        	return 1f / 64f;
		
	}
	
	public static int tryParseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
     * Finds the highest block on the x and z coordinate that is solid or liquid, and returns its y coord.
     */
    public static BlockPos getTopSolidOrLiquidBlock(World world, BlockPos pos)
    {
        Chunk chunk = world.getChunk(pos);
        BlockPos blockpos;
        BlockPos blockpos1;

        for (blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1)
        {
            blockpos1 = blockpos.down();
            IBlockState state = chunk.getBlockState(blockpos1);

            if (state.getMaterial().blocksMovement() && !state.getMaterial().equals(Material.LEAVES) && !state.getBlock().isFoliage(state, world, blockpos1))
            {
                break;
            }
        }

        return blockpos;
    }
}
