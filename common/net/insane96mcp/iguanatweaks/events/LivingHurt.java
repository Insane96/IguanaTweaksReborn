package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingHurt {

	@SubscribeEvent
	public static void EventLivingHurt(LivingHurtEvent event) {
		if (event.getEntityLiving().world.isRemote)
			return;
		ModuleMovementRestriction.DamageSlowness(event.getEntityLiving(), event.getAmount());
	}
}
