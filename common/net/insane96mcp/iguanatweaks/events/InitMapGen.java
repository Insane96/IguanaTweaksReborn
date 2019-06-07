package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.mapgen.MapGenNoVillage;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class InitMapGen {
	@SubscribeEvent
	public static void EventInitMapGen(InitMapGenEvent event) {
		if (!Properties.config.misc.genVillages && event.getType().equals(EventType.VILLAGE)) {
			event.setNewGen(new MapGenNoVillage());
		}
	}
}
