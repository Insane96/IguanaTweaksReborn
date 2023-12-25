package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SaturationSyncMessage {
	float saturationLevel;

	public SaturationSyncMessage(float saturationLevel) {
		this.saturationLevel = saturationLevel;
	}

	public static void encode(SaturationSyncMessage pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.saturationLevel);
	}

	public static SaturationSyncMessage decode(FriendlyByteBuf buf) {
		return new SaturationSyncMessage(buf.readFloat());
	}

	public static void handle(final SaturationSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodData().saturationLevel = message.saturationLevel);
		ctx.get().setPacketHandled(true);
	}
}
