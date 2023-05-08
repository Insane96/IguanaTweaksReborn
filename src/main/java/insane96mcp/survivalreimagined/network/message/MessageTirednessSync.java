package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.sleeprespawn.utils.TirednessHelper;
import insane96mcp.survivalreimagined.network.NetworkHelper;
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
		ctx.get().enqueueWork(() -> TirednessHelper.set(NetworkHelper.getSidedPlayer(ctx.get()), message.tiredness));
		ctx.get().setPacketHandled(true);
	}
}
