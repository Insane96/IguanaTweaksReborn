package net.insane96mcp.iguanatweaks.events;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.modules.ModuleSleepRespawn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
public class PlayerLogInRespawn {
	@SubscribeEvent
	public static void eventPlayerLogin(PlayerLoggedInEvent event) {
		//ModuleSleepRespawn.processSpawn(event.getPlayer());
	}
	
	@SubscribeEvent
	public static void eventPlayerRespawn(PlayerRespawnEvent event) {
		ModuleSleepRespawn.processRespawn((EntityPlayerMP) event.getPlayer());
	}
}
