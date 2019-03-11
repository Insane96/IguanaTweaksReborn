package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleSleepRespawn;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class PlayerSleepInBed {
	@SubscribeEvent
	public static void eventPlayerSleepInBed(PlayerSleepInBedEvent event) {
		ModuleSleepRespawn.disabledSpawnPoint(event);
	}
}
