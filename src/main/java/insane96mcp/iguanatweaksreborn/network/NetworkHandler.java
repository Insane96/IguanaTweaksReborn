package insane96mcp.iguanatweaksreborn.network;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = Integer.toString(3);
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "network_channel"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void init() {
		CHANNEL.registerMessage(1, ExhaustionSyncMessage.class, ExhaustionSyncMessage::encode, ExhaustionSyncMessage::decode, ExhaustionSyncMessage::handle);
		CHANNEL.registerMessage(2, SaturationSyncMessage.class, SaturationSyncMessage::encode, SaturationSyncMessage::decode, SaturationSyncMessage::handle);
		CHANNEL.registerMessage(3, TirednessSyncMessage.class, TirednessSyncMessage::encode, TirednessSyncMessage::decode, TirednessSyncMessage::handle);
		CHANNEL.registerMessage(4, StaminaSyncMessage.class, StaminaSyncMessage::encode, StaminaSyncMessage::decode, StaminaSyncMessage::handle);
		CHANNEL.registerMessage(5, FoodRegenSyncMessage.class, FoodRegenSyncMessage::encode, FoodRegenSyncMessage::decode, FoodRegenSyncMessage::handle);
		CHANNEL.registerMessage(6, SpawnerStatusSyncMessage.class, SpawnerStatusSyncMessage::encode, SpawnerStatusSyncMessage::decode, SpawnerStatusSyncMessage::handle);
		CHANNEL.registerMessage(7, SyncInvulnerableTimeMessage.class, SyncInvulnerableTimeMessage::encode, SyncInvulnerableTimeMessage::decode, SyncInvulnerableTimeMessage::handle);
		CHANNEL.registerMessage(8, GlobalHardnessSyncMessage.class, GlobalHardnessSyncMessage::encode, GlobalHardnessSyncMessage::decode, GlobalHardnessSyncMessage::handle);
		CHANNEL.registerMessage(9, JumpMidAirMessage.class, JumpMidAirMessage::encode, JumpMidAirMessage::decode, JumpMidAirMessage::handle);
		CHANNEL.registerMessage(10, SyncMovementDirection.class, SyncMovementDirection::encode, SyncMovementDirection::decode, SyncMovementDirection::handle);
		CHANNEL.registerMessage(11, SyncForgeStatus.class, SyncForgeStatus::encode, SyncForgeStatus::decode, SyncForgeStatus::handle);
		CHANNEL.registerMessage(12, ElectrocutionParticleMessage.class, ElectrocutionParticleMessage::encode, ElectrocutionParticleMessage::decode, ElectrocutionParticleMessage::handle);
		CHANNEL.registerMessage(13, SyncSREnchantingTableStatus.class, SyncSREnchantingTableStatus::encode, SyncSREnchantingTableStatus::decode, SyncSREnchantingTableStatus::handle);
		CHANNEL.registerMessage(14, SyncAnvilRepair.class, SyncAnvilRepair::encode, SyncAnvilRepair::decode, SyncAnvilRepair::handle);
		CHANNEL.registerMessage(15, StackSizesSync.class, StackSizesSync::encode, StackSizesSync::decode, StackSizesSync::handle);
		CHANNEL.registerMessage(16, ServerboundSetSRBeacon.class, ServerboundSetSRBeacon::encode, ServerboundSetSRBeacon::decode, ServerboundSetSRBeacon::handle);
	}
}
