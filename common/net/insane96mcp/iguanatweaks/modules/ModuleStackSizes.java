package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.lib.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModuleStackSizes {
	
	public static void ProcessBlocks() {
    	if (Properties.StackSizes.blockDividerMax <= 1
    		|| (Properties.StackSizes.blockDividerMin > Properties.StackSizes.blockDividerMax))
    		return;
    	
    	for (Block block : Block.REGISTRY)
    	{
    		Item item = Item.getItemFromBlock(block);

    		if (item == null)
    			continue;
    		
			float blockWeight = Utils.getBlockWeight(block);
	        
			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / Properties.StackSizes.blockDividerMin;
	        
	        if (blockWeight > 0f) {
	        	stackSize = (int) (maxStackSize / (Properties.StackSizes.blockDividerMax * blockWeight));
	        	if (stackSize > maxStackSize / Properties.StackSizes.blockDividerMin) 
	        		stackSize = maxStackSize / Properties.StackSizes.blockDividerMin;
	        }
	        
	        if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
        		if (Properties.StackSizes.logChanges)
        			IguanaTweaks.logger.debug("Reducing stack size of block " + item.getUnlocalizedName()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
    
	public static void ProcessItems() {
    	if (Properties.StackSizes.itemDivider <= 1)
    		return;
    	
    	for (Item item : Item.REGISTRY)
    	{
    		if (item == null)
    			continue;

    		if (item instanceof ItemBlock)
    			continue;

			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / Properties.StackSizes.itemDivider;
	        
    		if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
    			if (Properties.StackSizes.logChanges) 
    				IguanaTweaks.logger.debug("Reducing stack size of item " + item.getUnlocalizedName()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
}
