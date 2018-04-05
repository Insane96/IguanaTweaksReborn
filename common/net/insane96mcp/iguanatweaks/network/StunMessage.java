package net.insane96mcp.iguanatweaks.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

}
