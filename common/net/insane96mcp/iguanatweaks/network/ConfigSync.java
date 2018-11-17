package net.insane96mcp.iguanatweaks.network;

import io.netty.buffer.ByteBuf;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigSync implements IMessage {

	public boolean lessObiviousSilverfish;
	public float multiplier;
	public boolean blockListIsWhitelist;
	public String blockList;
	public String blockHardness;
	
	public ConfigSync() { 
		lessObiviousSilverfish = true;
		multiplier = 4.0f;
		blockListIsWhitelist = false;
		blockList = "";
		blockHardness = "";
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		lessObiviousSilverfish = buf.readBoolean();
		multiplier = buf.readFloat();
		blockListIsWhitelist = buf.readBoolean();
		blockList = ByteBufUtils.readUTF8String(buf);
		blockHardness = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(lessObiviousSilverfish);
		buf.writeFloat(multiplier);
		buf.writeBoolean(blockListIsWhitelist);
		ByteBufUtils.writeUTF8String(buf, blockList);
		ByteBufUtils.writeUTF8String(buf, blockHardness);
	}
	
	public static class Handler implements IMessageHandler<ConfigSync, IMessage> {

		@Override
		public IMessage onMessage(ConfigSync message, MessageContext ctx) {
			IThreadListener iThreadListener = Minecraft.getMinecraft();
			iThreadListener.addScheduledTask(new Runnable() {

				@Override
				public void run() {
					Properties.config.misc.lessObviousSilverfish = message.lessObiviousSilverfish;
					Properties.config.hardness.multiplier = message.multiplier;
					Properties.config.hardness.blockListIsWhitelist = message.blockListIsWhitelist;
					if (!message.blockList.isEmpty()) {
						String[] blockList = message.blockList.split("\r\n");
						Properties.config.hardness.blockList = blockList;
					}
					if (!message.blockHardness.isEmpty()) {
						String[] blockHardness = message.blockHardness.split("\r\n");
						Properties.config.hardness.blockHardness = blockHardness;
					}
				}
				
			});
			return null;
		}
		
	}
}
