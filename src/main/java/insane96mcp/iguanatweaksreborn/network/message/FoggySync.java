package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.world.weather.ClientWeather;
import insane96mcp.iguanatweaksreborn.module.world.weather.Foggy;
import insane96mcp.iguanatweaksreborn.module.world.weather.WeatherSavedData;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FoggySync {
	private WeatherSavedData.FoggyData foggyData;

	public FoggySync(WeatherSavedData.FoggyData foggyData) {
		this.foggyData = foggyData;
	}

	public static void encode(FoggySync pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.foggyData.timer);
		buf.writeInt(pkt.foggyData.targetTime);
		buf.writeEnum(pkt.foggyData.current);
		buf.writeEnum(pkt.foggyData.target);
	}

	public static FoggySync decode(FriendlyByteBuf buf) {
		return new FoggySync(new WeatherSavedData.FoggyData(buf.readInt(), buf.readInt(), buf.readEnum(Foggy.class), buf.readEnum(Foggy.class)));
	}

	public static void handle(final FoggySync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientWeather.updateFoggy(message.foggyData);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerPlayer player, WeatherSavedData.FoggyData foggyData) {
		Object msg = new FoggySync(foggyData);
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
