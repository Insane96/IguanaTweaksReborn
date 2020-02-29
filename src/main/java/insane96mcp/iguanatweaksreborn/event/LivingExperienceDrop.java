package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.ExperienceModule;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class LivingExperienceDrop {

	@SubscribeEvent
	public static void EventLivingExperienceDrop(LivingExperienceDropEvent event) {
		ExperienceModule.mobsFromSpawnersXpDrop(event);
	}
}