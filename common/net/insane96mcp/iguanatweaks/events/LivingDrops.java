package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingDrops {

	@SubscribeEvent
	public static void eventLivingDrops(LivingDropsEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living == null)
			return;
		/*ModuleDrops.restrictedDrops(living, event.getDrops());
		ModuleDrops.mobDrop(living, event.getDrops());
		ModuleDrops.playerDrop(living, event.getDrops());*/
	}
}
