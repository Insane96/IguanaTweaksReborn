package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHurt {

	@SubscribeEvent
	public static void EventLivingHurt(LivingHurtEvent event) {
		ModuleMovementRestriction.DamageSlowness(event.getEntityLiving(), event.getAmount());
	}
}
