package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ModuleExperience {
	public static void XpLifespan(Entity entity) {
		if (!Properties.Global.experience)
			return;
		
		if (!(entity instanceof EntityXPOrb))
			return;
		
		EntityXPOrb xpOrb = (EntityXPOrb)entity;
		
		if (Properties.Experience.lifespan == -1)
			xpOrb.xpOrbAge = 32768;
		else
			xpOrb.xpOrbAge = 6000 - Properties.Experience.lifespan;
    }
    
    public static void XpDropPercentage(Entity entity) {
		if (!Properties.Global.experience)
			return;
		
        if (Properties.Experience.percentageAll == 100.0f)
            return;

        if (!(entity instanceof EntityXPOrb))
			return;
		
		EntityXPOrb xpOrb = (EntityXPOrb)entity;
		
		if (Properties.Experience.percentageAll == 0.0f)
			entity.world.removeEntity(xpOrb);
		else
			xpOrb.xpValue = Math.round(xpOrb.xpValue * (Properties.Experience.percentageAll / 100f)); 
    }
    
    public static void XpDropFromSpawner(LivingExperienceDropEvent event) {
    	if (!Properties.Global.experience)
			return;
		
        if (Properties.Experience.percentageFromSpawner == 100.0f)
            return;

        EntityLivingBase living = event.getEntityLiving();
        NBTTagCompound tags = living.getEntityData();
        
        if (!tags.getBoolean("iguanatweaks:spawnedFromSpawner"))
        	return;
		
        int actualXp = event.getDroppedExperience();
        int newXp = (int) (actualXp * (Properties.Experience.percentageFromSpawner / 100f));
        event.setDroppedExperience(newXp);
    }

    public static void XpDropOre(BreakEvent event) {
		if (!Properties.Global.experience)
			return;
		
        if (Properties.Experience.percentageOre == 100.0f)
            return;

        event.setExpToDrop(Math.round(event.getExpToDrop() * (Properties.Experience.percentageOre / 100f)));
    }
    
    public static void CheckFromSpawner(LivingSpawnEvent.SpecialSpawn event) {
    	if (event.getSpawner() == null)
			return;
		
		event.getEntityLiving().getEntityData().setBoolean("iguanatweaks:spawnedFromSpawner", true);
    }
}
