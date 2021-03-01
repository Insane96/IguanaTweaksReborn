package net.insane96mcp.iguanatweaks.modules;

import java.util.ArrayList;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

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
    			item.setMaxStackSize(stackSize);
    		}
    	}
	}
	
	public static void ProcessCustom() {
		if (!ModConfig.config.global.stackSize)
			return;
		
		if (customStackSizes.size() == 0)
			return;
		
		for (StackSize stackSize : customStackSizes) {
			Item item = Item.getByNameOrId(stackSize.id.toString());
			item.setMaxStackSize(stackSize.stackSize);
		}
	}
	
	private static ArrayList<StackSize> customStackSizes = new ArrayList<>();
	
	public static void loadCustomStackSizes() {
		customStackSizes.clear();
		for (String line : ModConfig.config.stackSizes.customStackList) {
        	if (line.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Custom Stack Size] Empty line found. Ignoring ...");
				continue;
        	}
        	
			String[] split = line.split(",");
			if (split.length != 2) {
				IguanaTweaks.logger.error("[Custom Stack Size] Failed to parse line " + line + ". Expected 2 arguments, got " + split.length);
				continue;
			}
			
			ResourceLocation id = new ResourceLocation(split[0]);
			int stackSize;
			
        	try {
				stackSize = Integer.parseInt(split[1]);
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[Custom Stack Size] Failed to parse stack size: " + line);
				continue;
			}
        	
        	StackSize.addCustomStackSize(new StackSize(id, stackSize));
		}
	}
	
	public static class StackSize {
		public ResourceLocation id;
		public int stackSize;
		
		public StackSize(ResourceLocation id, int stackSize) {
			this.id = id;
			this.stackSize = stackSize;
		}
		
		public static void addCustomStackSize(StackSize s) {
			boolean contains = false;
			for (StackSize stackSize : customStackSizes) {
				if (stackSize.id.equals(s.id)) {
					contains = true;
					IguanaTweaks.logger.warn("[Custom Stack Sizes] Duplicated entiry " + s.id);
					break;
				}
			}
			
			if (!contains)
				customStackSizes.add(s);
		}
	}
}
