package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingExperienceDrop {

	@SubscribeEvent
	public static void EventLivingExperienceDrop(LivingExperienceDropEvent event) {
		//ModuleExperience.XpDropFromSpawner(event);
	}
}
