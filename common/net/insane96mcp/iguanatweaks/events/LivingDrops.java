package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.modules.ModuleDrops;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingDrops {

	@SubscribeEvent
	public static void EventLivingDrops(LivingDropsEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living == null)
			return;
		ModuleDrops.RestrictedDrops(living, event.getDrops());
		ModuleDrops.MobDrop(living, event.getDrops());
		ModuleDrops.PlayerDrop(living, event.getDrops());
	}
}
