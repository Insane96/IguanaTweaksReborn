package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleExperience;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class Break {
	
	@SubscribeEvent
	public static void EventBreak(BreakEvent event) {
        ModuleExperience.XpDropOre(event);
        ModuleMisc.ExhaustionOnBlockBreak(event);
	}
}
