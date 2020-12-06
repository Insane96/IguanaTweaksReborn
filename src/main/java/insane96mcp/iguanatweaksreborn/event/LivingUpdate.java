package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class LivingUpdate {

	@SubscribeEvent
	public static void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
		FarmingModule.Livestock.slowdownAnimalGrowth(event);
		FarmingModule.Livestock.slowdownBreeding(event);
		FarmingModule.Livestock.slowdownEggLay(event);
		FarmingModule.Livestock.cowMilkTick(event);
	}
}
