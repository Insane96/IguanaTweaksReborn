package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class Sleep {
	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		//SleepRespawnModule.disableSleeping(event);
	}
}
