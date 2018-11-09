package net.insane96mcp.iguanatweaks.network;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(IguanaTweaks.MOD_ID);
	private static int discriminator = 0;
	
	public static void Init() {
		INSTANCE.registerMessage(StunMessage.Handler.class, StunMessage.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(HideArmorTimestamp.Handler.class, HideArmorTimestamp.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(ConfigSync.Handler.class, ConfigSync.class, discriminator++, Side.CLIENT);
	}
	
	public static void SendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}
	
	public static void SendToClient(IMessage message, EntityPlayerMP player) {
		INSTANCE.sendTo(message, player);
	}
	
	public static void SendToAll(IMessage message) {
		INSTANCE.sendToAll(message);
	}
}
