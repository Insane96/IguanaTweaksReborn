package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingUpdate {
	
	@SubscribeEvent
	public static void EventLivingUpdate(LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer){
			System.out.println(event.getEntityLiving().getEntityData());
		}
		ModuleMovementRestriction.Apply(event.getEntityLiving());
	}
}