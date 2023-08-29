package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExhaustionSyncMessage {
	float exhaustionLevel;

	public ExhaustionSyncMessage(float exhaustionLevel) {
		this.exhaustionLevel = exhaustionLevel;
	}

	public static void encode(ExhaustionSyncMessage pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.exhaustionLevel);
	}

	public static ExhaustionSyncMessage decode(FriendlyByteBuf buf) {
		return new ExhaustionSyncMessage(buf.readFloat());
	}

	public static void handle(final ExhaustionSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodData().exhaustionLevel = message.exhaustionLevel);
		ctx.get().setPacketHandled(true);
	}
}
