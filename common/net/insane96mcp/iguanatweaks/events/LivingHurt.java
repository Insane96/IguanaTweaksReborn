package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingHurt {

	@SubscribeEvent
	public static void EventLivingHurt(LivingHurtEvent event) {
		/*if (event.getEntityLiving().world.isRemote){
			ModuleHud.DamagedPlayer(event.getEntityLiving());
		}
		ModuleMovementRestriction.Stun(event.getEntityLiving(), event.getAmount());*/
	}
}
