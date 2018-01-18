package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingUpdate {
	
	@SubscribeEvent
	public static void EventLivingUpdate(LivingUpdateEvent event) {
		ModuleMovementRestriction.Apply(event.getEntityLiving());
	}
}