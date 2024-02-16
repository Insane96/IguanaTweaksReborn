package insane96mcp.iguanatweaksreborn.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExhaustionSync {
	float exhaustionLevel;

	public ExhaustionSync(float exhaustionLevel) {
		this.exhaustionLevel = exhaustionLevel;
	}

	public static void encode(ExhaustionSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.exhaustionLevel);
	}

	public static ExhaustionSync decode(FriendlyByteBuf buf) {
		return new ExhaustionSync(buf.readFloat());
	}

	public static void handle(final ExhaustionSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getFoodData().exhaustionLevel = message.exhaustionLevel);
		ctx.get().setPacketHandled(true);
	}
}
