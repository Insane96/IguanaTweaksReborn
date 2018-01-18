package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHurt {

	@SubscribeEvent
	public static void EventLivingHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer){
			System.out.println(event.getEntityLiving().getEntityData());
		}
		ModuleMovementRestriction.DamageSlowness(event.getEntityLiving(), event.getAmount());
	}
}
