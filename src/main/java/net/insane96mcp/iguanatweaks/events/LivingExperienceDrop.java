package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleExperience;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingExperienceDrop {

	@SubscribeEvent
	public static void EventLivingExperienceDrop(LivingExperienceDropEvent event) {
		ModuleExperience.XpDropFromSpawner(event);
	}
}
