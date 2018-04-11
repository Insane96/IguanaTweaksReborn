package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleExperience;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingSpawn {
	@SubscribeEvent
	public static void SpawnSpecial(LivingSpawnEvent.SpecialSpawn event) {
		ModuleExperience.CheckFromSpawner(event);
	}
}
