package insane96mcp.iguanatweaksreborn.network;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.JumpMidAirMessage;
import insane96mcp.iguanatweaksreborn.network.message.SyncAnvilRepair;
import insane96mcp.iguanatweaksreborn.network.message.SyncInvulnerableTimeMessage;
import insane96mcp.iguanatweaksreborn.network.message.SyncItemStatistics;
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

	private static int index = 0;

	public static void init() {
		CHANNEL.registerMessage(++index, JumpMidAirMessage.class, JumpMidAirMessage::encode, JumpMidAirMessage::decode, JumpMidAirMessage::handle);
		CHANNEL.registerMessage(++index, SyncInvulnerableTimeMessage.class, SyncInvulnerableTimeMessage::encode, SyncInvulnerableTimeMessage::decode, SyncInvulnerableTimeMessage::handle);
		CHANNEL.registerMessage(++index, SyncAnvilRepair.class, SyncAnvilRepair::encode, SyncAnvilRepair::decode, SyncAnvilRepair::handle);
		CHANNEL.registerMessage(++index, SyncItemStatistics.class, SyncItemStatistics::encode, SyncItemStatistics::decode, SyncItemStatistics::handle);
	}
}
