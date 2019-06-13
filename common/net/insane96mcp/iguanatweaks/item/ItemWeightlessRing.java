package net.insane96mcp.iguanatweaks.item;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemWeightlessRing extends Item {
	public ItemWeightlessRing() {
		super();
		
		ResourceLocation id = new ResourceLocation(IguanaTweaks.MOD_ID, "weightless_ring");
		
		setRegistryName(id);
		setTranslationKey(id.toString());
		setCreativeTab(CreativeTabs.TOOLS);
		
		ModItems.register(this);
	}
}
