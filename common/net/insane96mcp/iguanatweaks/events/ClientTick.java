package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTick {

	@SubscribeEvent
	public static void EventClientTick(TickEvent.ClientTickEvent event) {
		ModuleHud.HotbarCheckKeyPress(event.phase);
	}
}
