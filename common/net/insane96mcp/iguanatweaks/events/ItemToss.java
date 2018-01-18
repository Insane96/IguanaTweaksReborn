package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleDrops;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemToss {
	
	@SubscribeEvent
	public static void EventItemToss(ItemTossEvent event) {
		ModuleDrops.PlayerToss(event.getEntityItem());
	}
}
