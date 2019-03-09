package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class ItemToss {
	
	@SubscribeEvent
	public static void EventItemToss(ItemTossEvent event) {
		//ModuleDrops.PlayerToss(event.getEntityItem());
	}
}
