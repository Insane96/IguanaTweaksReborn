package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.TirednessHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TirednessSync {
	float tiredness;

	public TirednessSync(float tiredness) {
		this.tiredness = tiredness;
	}

	public static void encode(TirednessSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.tiredness);
	}

	public static TirednessSync decode(FriendlyByteBuf buf) {
		return new TirednessSync(buf.readFloat());
	}

	public static void handle(final TirednessSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> TirednessHandler.set(Minecraft.getInstance().player, message.tiredness));
		ctx.get().setPacketHandled(true);
	}
}
