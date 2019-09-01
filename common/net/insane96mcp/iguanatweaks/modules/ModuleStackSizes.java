package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModuleStackSizes {
	
	public static void ProcessBlocks() {
		if (!ModConfig.config.global.stackSize)
			return;
		
    	if (ModConfig.config.stackSizes.blockDividerMax <= 1
    		|| (ModConfig.config.stackSizes.blockDividerMin > ModConfig.config.stackSizes.blockDividerMax))
    		return;
    	
    	for (Block block : Block.REGISTRY)
    	{
    		Item item = Item.getItemFromBlock(block);

    		if (item == null)
    			continue;
    		
			float blockWeight = Utils.GetBlockWeight(block);
	        
			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / ModConfig.config.stackSizes.blockDividerMin;
	        
	        if (blockWeight > 0f) {
	        	stackSize = (int) (maxStackSize / (ModConfig.config.stackSizes.blockDividerMax * blockWeight));
	        	if (stackSize > maxStackSize / ModConfig.config.stackSizes.blockDividerMin) 
	        		stackSize = maxStackSize / ModConfig.config.stackSizes.blockDividerMin;
	        }
	        
	        if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
        		if (ModConfig.config.stackSizes.logChanges)
        			IguanaTweaks.logger.info("Reducing stack size of block " + item.getTranslationKey()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
    
	public static void ProcessItems() {
		if (!ModConfig.config.global.stackSize)
			return;
		
    	if (ModConfig.config.stackSizes.itemDivider <= 1)
    		return;
    	
    	for (Item item : Item.REGISTRY)
    	{
    		if (item == null)
    			continue;

    		if (item instanceof ItemBlock)
    			continue;

			int maxStackSize = item.getItemStackLimit();
	        int stackSize = maxStackSize / ModConfig.config.stackSizes.itemDivider;
	        
    		if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
    			if (ModConfig.config.stackSizes.logChanges) 
    				IguanaTweaks.logger.info("Reducing stack size of item " + item.getTranslationKey()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
	
	public static void ProcessCustom() {
		if (!ModConfig.config.global.stackSize)
			return;
		
		if (ModConfig.config.stackSizes.customStackList.length == 0)
			return;
		
		for (int i = 0; i < ModConfig.config.stackSizes.customStackList.length; i++) {
			try {
				String[] split = ModConfig.config.stackSizes.customStackList[i].split(",");
				if (split.length == 0)
					continue;
				String name = split[0];
				int stackSize = Integer.parseInt(split[1]);
				Item item = Item.getByNameOrId(name);
				item.setMaxStackSize(stackSize);
				if (ModConfig.config.stackSizes.logChanges) 
					IguanaTweaks.logger.info("Reducing stack size by custom of item " + item.getTranslationKey()  + " to " + stackSize);
			}
			catch (Exception exception) {
				System.err.println("Failed to parse: " + ModConfig.config.stackSizes.customStackList[i] + " " + exception);
			}
		}
	}
}
