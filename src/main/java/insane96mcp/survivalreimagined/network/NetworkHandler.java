package insane96mcp.survivalreimagined.network;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
		CHANNEL.registerMessage(4, StaminaSyncMessage.class, StaminaSyncMessage::encode, StaminaSyncMessage::decode, StaminaSyncMessage::handle);
		CHANNEL.registerMessage(5, MessageFoodRegenSync.class, MessageFoodRegenSync::encode, MessageFoodRegenSync::decode, MessageFoodRegenSync::handle);
		CHANNEL.registerMessage(6, MessageSpawnerStatusSync.class, MessageSpawnerStatusSync::encode, MessageSpawnerStatusSync::decode, MessageSpawnerStatusSync::handle);
		CHANNEL.registerMessage(7, JsonConfigSyncMessage.class, JsonConfigSyncMessage::encode, JsonConfigSyncMessage::decode, JsonConfigSyncMessage::handle);
		CHANNEL.registerMessage(8, SyncInvulnerableTimeMessage.class, SyncInvulnerableTimeMessage::encode, SyncInvulnerableTimeMessage::decode, SyncInvulnerableTimeMessage::handle);
		CHANNEL.registerMessage(9, GlobalHardnessSyncMessage.class, GlobalHardnessSyncMessage::encode, GlobalHardnessSyncMessage::decode, GlobalHardnessSyncMessage::handle);
		CHANNEL.registerMessage(10, JumpMidAirMessage.class, JumpMidAirMessage::encode, JumpMidAirMessage::decode, JumpMidAirMessage::handle);
		CHANNEL.registerMessage(11, SyncMovementDirection.class, SyncMovementDirection::encode, SyncMovementDirection::decode, SyncMovementDirection::handle);
		CHANNEL.registerMessage(12, SyncForgeStatus.class, SyncForgeStatus::encode, SyncForgeStatus::decode, SyncForgeStatus::handle);
		CHANNEL.registerMessage(12, ElectrocutionParticleMessage.class, ElectrocutionParticleMessage::encode, ElectrocutionParticleMessage::decode, ElectrocutionParticleMessage::handle);
	}
}
