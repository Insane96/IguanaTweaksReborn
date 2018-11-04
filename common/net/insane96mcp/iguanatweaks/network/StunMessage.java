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

public class StunMessage implements IMessage {

	public StunMessage() {}
	
	public int duration;
	public StunMessage(int duration) {
		this.duration = duration;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.duration = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(duration);
	}
	
	public static class Handler implements IMessageHandler<StunMessage, IMessage> {

		@Override
		public IMessage onMessage(StunMessage message, MessageContext ctx) {
			IThreadListener iThreadListener = Minecraft.getMinecraft();
			iThreadListener.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
					int duration = message.duration;
					IPlayerData playerData = clientPlayer.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
			    	playerData.setDamageSlownessDuration(duration);
				}
			});
			
			return null;
		}

	}


}
