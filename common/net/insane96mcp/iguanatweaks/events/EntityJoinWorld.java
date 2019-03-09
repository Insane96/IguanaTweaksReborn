package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class EntityJoinWorld {
	
	@SubscribeEvent
	public static void EventEntityJoinWorld(EntityJoinWorldEvent event) {
		//ModuleExperience.XpLifespan(event.getEntity());
		//ModuleExperience.XpDropPercentage(event.getEntity());
	}
}
