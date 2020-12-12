package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class EntityInteract {

	@SubscribeEvent
	public static void entityInteractEvent(PlayerInteractEvent.EntityInteract event) {
		FarmingModule.Livestock.onCowMilk(event);

		/*if (event.getTarget() instanceof LivingEntity) {
			LivingEntity entity = (LivingEntity) event.getTarget();
			entity.startSleeping(event.getTarget().getPosition());
			if (!entity.getBedPosition().map((p_241350_1_) -> ForgeEventFactory.fireSleepingLocationCheck(entity, p_241350_1_)).orElse(false))
				entity.wakeUp();
			else
				((MobEntity)entity).setNoAI(true);
		}*/
	}
}
