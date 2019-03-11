package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.init.ModConfig;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModuleStackSizes {
	
	public static void processBlocks() {
		if (!ModConfig.Global.stackSize.get())
			return;
		
    	if (ModConfig.StackSizes.blockDividerMax.get() <= 1
    		|| (ModConfig.StackSizes.blockDividerMin.get() > ModConfig.StackSizes.blockDividerMax.get()))
    		return;
    	
    	for (Block block : ForgeRegistries.BLOCKS)
    	{
    		Item item = Item.getItemFromBlock(block);

    		if (item == null)
    			continue;
    		
    		//TODO use state get material instead of block get material
			float blockWeight = Utils.getBlockWeight(block);
	        
			//TODO Will surely crash with some mods
			int maxStackSize = item.getItemStackLimit(null);
	        int stackSize = maxStackSize / ModConfig.StackSizes.blockDividerMin.get();
	        
	        if (blockWeight > 0f) {
	        	stackSize = (int) (maxStackSize / (ModConfig.StackSizes.blockDividerMax.get() * blockWeight));
	        	if (stackSize > maxStackSize / ModConfig.StackSizes.blockDividerMin.get()) 
	        		stackSize = maxStackSize / ModConfig.StackSizes.blockDividerMin.get();
	        }
	        
	        if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
        		/*if (ModConfig.StackSizes.logChanges)
        			IguanaTweaks.logger.info("Reducing stack size of block " + item.getTranslationKey()  + " to " + stackSize);*/
    			ObfuscationReflectionHelper.setPrivateValue(Item.class, item, stackSize, "maxStackSize");
    			//item.setMaxStackSize(stackSize);
    		}
    	}
	}
    
	public static void processItems() {
		if (!ModConfig.Global.stackSize.get())
			return;
		
    	if (ModConfig.StackSizes.itemDivider.get() <= 1)
    		return;
    	
    	for (Item item : ForgeRegistries.ITEMS)
    	{
    		if (item == null)
    			continue;

    		if (item instanceof ItemBlock)
    			continue;

			//TODO Will surely crash with some mods
			int maxStackSize = item.getItemStackLimit(null);
	        int stackSize = maxStackSize / ModConfig.StackSizes.itemDivider.get();
	        
    		if (stackSize < 1) 
    			stackSize = 1;
    		if (stackSize > 64) 
    			stackSize = 64;
    		
    		if (stackSize < maxStackSize) 
    		{
    			/*if (ModConfig.StackSizes.logChanges) 
    				IguanaTweaks.logger.info("Reducing stack size of item " + item.getTranslationKey()  + " to " + stackSize);
    			item.setMaxStackSize(stackSize);*/
    			ObfuscationReflectionHelper.setPrivateValue(Item.class, item, stackSize, "maxStackSize");
    		}
    	}
	}
	
	public static void processCustom() {
		if (!ModConfig.Global.stackSize.get())
			return;
		
		if (ModConfig.StackSizes.customStackList.get().size() == 0)
			return;
		
		for (String line : ModConfig.StackSizes.customStackList.get()) {
			String[] split = line.split(",");
			if (split.length != 2) {
				IguanaTweaks.logger.error("[StackSizes] Failed to parse: " + line);
				continue;
			}
			
			ResourceLocation id = new ResourceLocation(split[0]);
			int stackSize = Utils.tryParseInt(split[1]);
			
			if (stackSize == 0) {
				IguanaTweaks.logger.error("[StackSizes] Invalid stack size for line: " + line);
				continue;
			}
			
			Item item = ForgeRegistries.ITEMS.getValue(id);
			ObfuscationReflectionHelper.setPrivateValue(Item.class, item, stackSize, "maxStackSize");
		}
	}
}
