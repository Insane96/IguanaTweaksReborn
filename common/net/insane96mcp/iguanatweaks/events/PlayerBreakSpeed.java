package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleHardness;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerBreakSpeed {

	@SubscribeEvent
	public static void EventBreakSpeed(BreakSpeed event) {
		ModuleHardness.ProcessGlobalHardness(event);
	}
}
