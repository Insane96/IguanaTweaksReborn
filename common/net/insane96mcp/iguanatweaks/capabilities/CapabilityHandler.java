package net.insane96mcp.iguanatweaks.capabilities;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {
	public static final ResourceLocation PLAYER_DATA_CAP = new ResourceLocation(IguanaTweaks.MOD_ID, "playerData");
	
	@SubscribeEvent
	public static void EventAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof EntityPlayer))
			return;
		
		event.addCapability(PLAYER_DATA_CAP, new PlayerDataProvider());
	}
	
	@SubscribeEvent
	public static void EventPlayerClone(PlayerEvent.Clone event) {
		EntityPlayer player = event.getEntityPlayer();
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		IPlayerData oldPlayerData = event.getOriginal().getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		playerData.setDamageSlownessDuration(oldPlayerData.getDamageSlownessDuration());
		playerData.setHideHealthBarLastTimestamp(oldPlayerData.getHideHealthBarLastTimestamp());
		playerData.setHideHungerBarLastTimestamp(oldPlayerData.getHideHungerBarLastTimestamp());
		playerData.setHideHotbarLastTimestamp(oldPlayerData.getHideHotbarLastTimestamp());
		playerData.setHideExperienceLastTimestamp(oldPlayerData.getHideExperienceLastTimestamp());
		playerData.setHideArmorLastTimestamp(oldPlayerData.getHideArmorLastTimestamp());
		playerData.setWeight(oldPlayerData.getWeight());
	}
}
