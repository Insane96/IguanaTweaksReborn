package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class PlayerLogInRespawn {
	@SubscribeEvent
	public static void EventPlayerLogin(PlayerLoggedInEvent event) {
		//ModuleSleepRespawn.ProcessSpawn(event.player);
		//ModuleGeneral.IncreasedStepHeight(event.player);
	}
	
	@SubscribeEvent
	public static void EventPlayerRespawn(PlayerRespawnEvent event) {
		//ModuleSleepRespawn.ProcessRespawn(event.player);
	}
}
