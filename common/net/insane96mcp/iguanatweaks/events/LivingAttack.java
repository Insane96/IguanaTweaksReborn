package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class LivingAttack {
	@SubscribeEvent
	public static void EventLivingAttack(LivingAttackEvent event) {
		/*if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();

			if (player.getHeldItemMainhand().isEmpty() || event.getAmount() <= 1f) {
				event.setCanceled(true);
				event.getEntityLiving().attackEntityFrom(DamageSource.GENERIC, event.getAmount());
				event.getEntityLiving().world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, 1.0f, 2.0f);
			}
		}*/
	}
}
