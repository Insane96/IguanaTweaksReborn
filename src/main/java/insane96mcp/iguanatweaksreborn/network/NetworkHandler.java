package insane96mcp.iguanatweaksreborn.network;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.AnvilRepairSync;
import insane96mcp.iguanatweaksreborn.network.message.InvulnerableTimeMessageSync;
import insane96mcp.iguanatweaksreborn.network.message.ItemStatisticsSync;
import insane96mcp.iguanatweaksreborn.network.message.StackSizesSync;
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
		CHANNEL.registerMessage(++index, InvulnerableTimeMessageSync.class, InvulnerableTimeMessageSync::encode, InvulnerableTimeMessageSync::decode, InvulnerableTimeMessageSync::handle);
		CHANNEL.registerMessage(++index, AnvilRepairSync.class, AnvilRepairSync::encode, AnvilRepairSync::decode, AnvilRepairSync::handle);
		CHANNEL.registerMessage(++index, ItemStatisticsSync.class, ItemStatisticsSync::encode, ItemStatisticsSync::decode, ItemStatisticsSync::handle);
		CHANNEL.registerMessage(++index, StackSizesSync.class, StackSizesSync::encode, StackSizesSync::decode, StackSizesSync::handle);
	}
}
