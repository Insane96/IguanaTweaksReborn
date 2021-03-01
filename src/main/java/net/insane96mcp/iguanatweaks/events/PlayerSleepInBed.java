package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleSleepRespawn;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class PlayerSleepInBed {
	@SubscribeEvent
	public static void EventPlayerSleepInBed(PlayerSleepInBedEvent event) {
		ModuleSleepRespawn.DisabledSpawnPoint(event);
	}
}
