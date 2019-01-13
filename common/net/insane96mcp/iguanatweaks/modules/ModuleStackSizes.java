package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModuleStackSizes {
	
	public static void ProcessBlocks() {
		if (!Properties.config.global.stackSize)
			return;
		
    	if (Properties.config.stackSizes.blockDividerMax <= 1
    		|| (Properties.config.stackSizes.blockDividerMin > Properties.config.stackSizes.blockDividerMax))
    		return;
    	
    	for (Block block : Block.REGISTRY)
    	{
    		Item item = Item.getItemFromBlock(block);

    		if (item == null)
    			continue;
    		
			float blockWeight = Utils.GetBlockWeight(block);
	        
			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / Properties.config.stackSizes.blockDividerMin;
	        
	        if (blockWeight > 0f) {
	        	stackSize = (int) (maxStackSize / (Properties.config.stackSizes.blockDividerMax * blockWeight));
	        	if (stackSize > maxStackSize / Properties.config.stackSizes.blockDividerMin) 
	        		stackSize = maxStackSize / Properties.config.stackSizes.blockDividerMin;
	        }
	        
	        if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
        		if (Properties.config.stackSizes.logChanges)
        			IguanaTweaks.logger.info("Reducing stack size of block " + item.getTranslationKey()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
    
	public static void ProcessItems() {
		if (!Properties.config.global.stackSize)
			return;
		
    	if (Properties.config.stackSizes.itemDivider <= 1)
    		return;
    	
    	for (Item item : Item.REGISTRY)
    	{
    		if (item == null)
    			continue;

    		if (item instanceof ItemBlock)
    			continue;

			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / Properties.config.stackSizes.itemDivider;
	        
    		if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
    			if (Properties.config.stackSizes.logChanges) 
    				IguanaTweaks.logger.info("Reducing stack size of item " + item.getTranslationKey()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
	
	public static void ProcessCustom() {
		if (!Properties.config.global.stackSize)
			return;
		
		if (Properties.config.stackSizes.customStackList.length == 0)
			return;
		
		for (int i = 0; i < Properties.config.stackSizes.customStackList.length; i++) {
			try {
				String[] split = Properties.config.stackSizes.customStackList[i].split(",");
				if (split.length == 0)
					continue;
				String name = split[0];
				int stackSize = Integer.parseInt(split[1]);
				Item item = Item.getByNameOrId(name);
				item.setMaxStackSize(stackSize);
				if (Properties.config.stackSizes.logChanges) 
					IguanaTweaks.logger.info("Reducing stack size by custom of item " + item.getTranslationKey()  + " to " + stackSize);
			}
			catch (Exception exception) {
				System.err.println("Failed to parse: " + Properties.config.stackSizes.customStackList[i] + " " + exception);
			}
		}
	}
}
