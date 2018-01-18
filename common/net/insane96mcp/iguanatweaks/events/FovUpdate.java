package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FovUpdate {

	@SubscribeEvent
	public static void EventFovUpdate(FOVUpdateEvent event) {
		ModuleGeneral.PreventFov(event);
	}
}
