package net.insane96mcp.iguanatweaks.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModuleDrops {
	public static void RestrictedDrops(EntityLivingBase living, List<EntityItem> drops) {
		if (!ModConfig.config.global.drops)
			return;
		
		if (ModConfig.config.drops.restrictedDrops.length == 0)
			return;
		
		if (living instanceof EntityPlayer)
			return;
		
		List<EntityItem> toRemove = new ArrayList<EntityItem>();
		List<String> restrictedDrops = Arrays.asList(ModConfig.config.drops.restrictedDrops);
		
		for (EntityItem item : drops) {
			ItemStack itemStack = item.getItem();
			if (itemStack == null)
				continue;
			
			String itemName = itemStack.getItem().getRegistryName().toString();
			
			if (restrictedDrops.contains(itemName)
				|| restrictedDrops.contains(itemName + ":" + itemStack.getItemDamage())) {
				toRemove.add(item);
			}
		}
		
		for (EntityItem remove : toRemove) {
			drops.remove(remove);
		}
	}
	
	public static void MobDrop(EntityLivingBase living, List<EntityItem> drops) {
		if (!ModConfig.config.global.drops)
			return;
		
		if (ModConfig.config.drops.itemLifespanMobDeath == 6000)
			return;
		
		if (living instanceof EntityPlayer)
			return;
		
		for (EntityItem item : drops) {
			item.lifespan = ModConfig.config.drops.itemLifespanMobDeath;
		}
	}
	
	public static void PlayerDrop(EntityLivingBase living, List<EntityItem> drops) {
		if (!ModConfig.config.global.drops)
			return;
		
		if (ModConfig.config.drops.itemLifespanPlayerDeath == 6000)
			return;
		
		if (!(living instanceof EntityPlayer))
			return;
		
		for (EntityItem item : drops) {
			item.lifespan = ModConfig.config.drops.itemLifespanPlayerDeath;
		}
	}
	
	public static void PlayerToss(EntityItem item) {
		if (!ModConfig.config.global.drops)
			return;
		
		if (ModConfig.config.drops.itemLifespanTossed == 6000)
			return;
		
		item.lifespan = ModConfig.config.drops.itemLifespanMobDeath;
	}
}
