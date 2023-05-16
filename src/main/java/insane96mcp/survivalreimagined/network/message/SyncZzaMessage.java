package insane96mcp.survivalreimagined.network.message;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class SyncZzaMessage {
	float zza;

	public SyncZzaMessage(float zza) {
		this.zza = zza;
	}

	public static void encode(SyncZzaMessage pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.zza);
	}

	public static SyncZzaMessage decode(FriendlyByteBuf buf) {
		return new SyncZzaMessage(buf.readFloat());
	}

	public static void handle(final SyncZzaMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender() != null)
				ctx.get().getSender().zza = message.zza;
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(LocalPlayer player, float zza) {
		Object msg = new SyncZzaMessage(zza);
		CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
	}
}
