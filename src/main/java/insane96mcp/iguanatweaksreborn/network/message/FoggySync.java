package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.world.weather.ClientWeather;
import insane96mcp.iguanatweaksreborn.module.world.weather.Foggy;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FoggySync {
	private final int foggyTimer;
	private final int foggyTargetTime;
	private final Foggy currentFoggy;
	private final Foggy targetFoggy;

	public FoggySync(int foggyTimer, int foggyTargetTime, Foggy currentFoggy, Foggy targetFoggy) {
		this.foggyTimer = foggyTimer;
		this.foggyTargetTime = foggyTargetTime;
		this.currentFoggy = currentFoggy;
		this.targetFoggy = targetFoggy;
	}

	public static void encode(FoggySync pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.foggyTimer);
		buf.writeInt(pkt.foggyTargetTime);
		buf.writeEnum(pkt.currentFoggy);
		buf.writeEnum(pkt.targetFoggy);
	}

	public static FoggySync decode(FriendlyByteBuf buf) {
		return new FoggySync(buf.readInt(), buf.readInt(), buf.readEnum(Foggy.class), buf.readEnum(Foggy.class));
	}

	public static void handle(final FoggySync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientWeather.updateFoggy(message.foggyTimer, message.foggyTargetTime, message.currentFoggy, message.targetFoggy);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerPlayer player, int foggyTimer, int foggyTargetTime, Foggy currentFoggy, Foggy targetFoggy) {
		Object msg = new FoggySync(foggyTimer, foggyTargetTime, currentFoggy, targetFoggy);
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
