package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.HungerHealthModule;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class PlayerTick {
	@SubscribeEvent
	public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
		HungerHealthModule.debuffsOnLowStats(event);
		/*if (event.player.world.isRemote())
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) event.player;

		if (player.ticksExisted % 20 != 0)
			return;

		int savedHunger = player.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "hunger");
		if (savedHunger != 0 && player.getFoodStats().getFoodLevel() > savedHunger){
			player.getFoodStats().addExhaustion(100);

		}
		player.getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "hunger", player.getFoodStats().getFoodLevel());*/

	}
}
