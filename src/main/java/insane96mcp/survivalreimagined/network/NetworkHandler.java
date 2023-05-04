package insane96mcp.survivalreimagined.network;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = Integer.toString(2);
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(SurvivalReimagined.MOD_ID, "network_channel"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void init() {
		CHANNEL.registerMessage(1, MessageExhaustionSync.class, MessageExhaustionSync::encode, MessageExhaustionSync::decode, MessageExhaustionSync::handle);
		CHANNEL.registerMessage(2, MessageSaturationSync.class, MessageSaturationSync::encode, MessageSaturationSync::decode, MessageSaturationSync::handle);
		CHANNEL.registerMessage(3, MessageTirednessSync.class, MessageTirednessSync::encode, MessageTirednessSync::decode, MessageTirednessSync::handle);
		CHANNEL.registerMessage(4, MessageStaminaSync.class, MessageStaminaSync::encode, MessageStaminaSync::decode, MessageStaminaSync::handle);
		CHANNEL.registerMessage(5, MessageFoodRegenSync.class, MessageFoodRegenSync::encode, MessageFoodRegenSync::decode, MessageFoodRegenSync::handle);
		CHANNEL.registerMessage(6, MessageSpawnerStatusSync.class, MessageSpawnerStatusSync::encode, MessageSpawnerStatusSync::decode, MessageSpawnerStatusSync::handle);
		CHANNEL.registerMessage(7, JsonConfigSyncMessage.class, JsonConfigSyncMessage::encode, JsonConfigSyncMessage::decode, JsonConfigSyncMessage::handle);
		CHANNEL.registerMessage(8, SyncInvulnerableTimeMessage.class, SyncInvulnerableTimeMessage::encode, SyncInvulnerableTimeMessage::decode, SyncInvulnerableTimeMessage::handle);
		CHANNEL.registerMessage(9, HarderCropsSyncMessage.class, HarderCropsSyncMessage::encode, HarderCropsSyncMessage::decode, HarderCropsSyncMessage::handle);
		CHANNEL.registerMessage(10, GlobalHardnessSyncMessage.class, GlobalHardnessSyncMessage::encode, GlobalHardnessSyncMessage::decode, GlobalHardnessSyncMessage::handle);
		MinecraftForge.EVENT_BUS.register(new NetworkHandler());
	}

	/*
	 * Sync exhaustion & saturation
	 */
	private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();
	private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();

	@SubscribeEvent
	public void onLivingTickEvent(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player))
			return;
		Float lastSaturationLevel = lastSaturationLevels.get(player.getUUID());
		if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodData().getSaturationLevel()) {
			Object msg = new MessageSaturationSync(player.getFoodData().getSaturationLevel());
			CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			lastSaturationLevels.put(player.getUUID(), player.getFoodData().getSaturationLevel());
		}
		Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUUID());
		float exhaustionLevel = player.getFoodData().exhaustionLevel;
		if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
			Object msg = new MessageExhaustionSync(exhaustionLevel);
			CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			lastExhaustionLevels.put(player.getUUID(), exhaustionLevel);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer))
			return;
		lastExhaustionLevels.remove(event.getEntity().getUUID());
		lastSaturationLevels.remove(event.getEntity().getUUID());
	}
}
