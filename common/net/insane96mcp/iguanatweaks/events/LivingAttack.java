package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleMisc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingAttack {
	@SubscribeEvent
	public static void EventLivingAttack(LivingAttackEvent event) {
		ModuleMisc.NoItemNoKnockback(event);
		
		if (event.getSource().getImmediateSource() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getSource().getImmediateSource();
			if (player.getDistance(event.getEntityLiving()) > 2.5f)
				event.setCanceled(true);
		}
	}
}
