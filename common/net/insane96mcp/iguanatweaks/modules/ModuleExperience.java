package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;

public class ModuleExperience {
	public static void XpLifespan(Entity entity) {
		if (!(entity instanceof EntityXPOrb))
			return;
		
		EntityXPOrb xpOrb = (EntityXPOrb)entity;
		
		if (Properties.Experience.lifespan == -1)
			xpOrb.xpOrbAge = 32768;
		else
			xpOrb.xpOrbAge = 6000 - Properties.Experience.lifespan;
    }
    
    public static void XpDropPercentage(Entity entity) {
        if (Properties.Experience.percentageAll == 100.0f)
            return;

        if (!(entity instanceof EntityXPOrb))
			return;
		
		EntityXPOrb xpOrb = (EntityXPOrb)entity;
		
		xpOrb.xpValue = Math.round(xpOrb.xpValue * (Properties.Experience.percentageAll / 100f)); 
    }

    public static void XpDropOre(BreakEvent event) {
        if (Properties.Experience.percentageOre == 100.0f)
            return;

        event.setExpToDrop(Math.round(event.getExpToDrop() * (Properties.Experience.percentageAll / 100f)));
    }
}
