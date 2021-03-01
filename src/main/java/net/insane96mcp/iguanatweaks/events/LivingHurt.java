package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleHud;
import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingHurt {

	@SubscribeEvent
	public static void EventLivingHurt(LivingHurtEvent event) {
		if (!event.getEntityLiving().world.isRemote){
			ModuleHud.DamagedPlayer(event.getEntityLiving());
		}
		ModuleMovementRestriction.Stun(event.getEntityLiving(), event.getAmount());
	}
}
