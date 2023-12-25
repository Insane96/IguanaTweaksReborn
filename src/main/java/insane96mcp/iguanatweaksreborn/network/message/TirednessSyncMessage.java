package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.TirednessHandler;
import insane96mcp.iguanatweaksreborn.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TirednessSyncMessage {
	float tiredness;

	public TirednessSyncMessage(float tiredness) {
		this.tiredness = tiredness;
	}

	public static void encode(TirednessSyncMessage pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.tiredness);
	}

	public static TirednessSyncMessage decode(FriendlyByteBuf buf) {
		return new TirednessSyncMessage(buf.readFloat());
	}

	public static void handle(final TirednessSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> TirednessHandler.set(NetworkHelper.getSidedPlayer(ctx.get()), message.tiredness));
		ctx.get().setPacketHandled(true);
	}
}
