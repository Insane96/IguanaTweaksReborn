package insane96mcp.survivalreimagined.network;

import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTirednessSync {
	float tiredness;

	public MessageTirednessSync(float tiredness) {
		this.tiredness = tiredness;
	}

	public static void encode(MessageTirednessSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.tiredness);
	}

	public static MessageTirednessSync decode(FriendlyByteBuf buf) {
		return new MessageTirednessSync(buf.readFloat());
	}

	public static void handle(final MessageTirednessSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getPersistentData().putFloat(Strings.Tags.TIREDNESS, message.tiredness));
		ctx.get().setPacketHandled(true);
	}
}
