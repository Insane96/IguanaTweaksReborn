package insane96mcp.iguanatweaksreborn.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SaturationSync {
	float saturationLevel;

	public SaturationSync(float saturationLevel) {
		this.saturationLevel = saturationLevel;
	}

	public static void encode(SaturationSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.saturationLevel);
	}

	public static SaturationSync decode(FriendlyByteBuf buf) {
		return new SaturationSync(buf.readFloat());
	}

	public static void handle(final SaturationSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getFoodData().saturationLevel = message.saturationLevel);
		ctx.get().setPacketHandled(true);
	}
}
