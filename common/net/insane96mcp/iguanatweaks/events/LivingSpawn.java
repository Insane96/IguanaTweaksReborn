package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingSpawn {
	@SubscribeEvent
	public static void SpawnSpecial(LivingSpawnEvent.SpecialSpawn event) {
		//ModuleExperience.CheckFromSpawner(event);
	}
}
