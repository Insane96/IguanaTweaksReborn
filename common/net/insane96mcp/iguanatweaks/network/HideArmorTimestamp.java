package net.insane96mcp.iguanatweaks.network;

import io.netty.buffer.ByteBuf;
import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HideArmorTimestamp implements IMessage {

	public int timestamp;
	
	public HideArmorTimestamp() { }
	
	public HideArmorTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.timestamp = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.timestamp);
	}

	
	public static class Handler implements IMessageHandler<HideArmorTimestamp, IMessage> {

		@Override
		public IMessage onMessage(HideArmorTimestamp message, MessageContext ctx) {
			IThreadListener iThreadListener = Minecraft.getMinecraft();
			iThreadListener.addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
					int timestamp = message.timestamp;
					IPlayerData playerData = clientPlayer.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
			    	
			    	playerData.setHideArmorLastTimestamp(timestamp);
				}
			});
			return null;
		}
		
	}
}
