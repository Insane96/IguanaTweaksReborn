package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.insane96mcp.iguanatweaks.modules.ModuleMovementRestriction;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingUpdate {
	
	@SubscribeEvent
	public static void EventLivingUpdate(LivingUpdateEvent event) {
		ModuleMovementRestriction.ApplyPlayer(event.getEntityLiving());
		ModuleMovementRestriction.ApplyEntity(event.getEntityLiving());
		ModuleMisc.ApplyPoison(event.getEntityLiving());
		/*
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if (player.getHeldItemMainhand().getItem().getRegistryName().toString().equals("minecraft:diamond_sword")) {
				IAttributeInstance reachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
				reachDistance.setBaseValue(3.5d);
			}
			else {
				IAttributeInstance reachDistance = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
				reachDistance.setBaseValue(5.0d);
			}
		}*/
	}
}