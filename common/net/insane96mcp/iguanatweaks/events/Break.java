package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleExperience;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Break {
	
	@SubscribeEvent
	public static void EventBreak(BreakEvent event) {
        ModuleExperience.XpDropOre(event);
	}
}
