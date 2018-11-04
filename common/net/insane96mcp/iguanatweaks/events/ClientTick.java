package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleGeneral;
import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class ClientTick {

	@SubscribeEvent
	public static void EventClientTick(TickEvent.ClientTickEvent event) {
		ModuleHud.HotbarCheckKeyPress(event.phase);
		
		if (event.phase.equals(TickEvent.Phase.START))
			++ModuleGeneral.updateCounter;
	}
}
