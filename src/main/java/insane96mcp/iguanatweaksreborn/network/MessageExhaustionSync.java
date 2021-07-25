package insane96mcp.iguanatweaksreborn.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/*
	Shamelessly copypasted from AppleSkin's code
 */
public class MessageExhaustionSync {
	float exhaustionLevel;

	public MessageExhaustionSync(float exhaustionLevel) {
		this.exhaustionLevel = exhaustionLevel;
	}

	public static void encode(MessageExhaustionSync pkt, PacketBuffer buf) {
		buf.writeFloat(pkt.exhaustionLevel);
	}

	public static MessageExhaustionSync decode(PacketBuffer buf) {
		return new MessageExhaustionSync(buf.readFloat());
	}

	public static void handle(final MessageExhaustionSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodStats().foodExhaustionLevel = message.exhaustionLevel);
		ctx.get().setPacketHandled(true);
	}
}