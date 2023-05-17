package insane96mcp.survivalreimagined.network.message;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class SyncMovementDirection {
	float xxa;
	float yya;
	float zza;

	public SyncMovementDirection(float xxa, float yya, float zza) {
		this.xxa = xxa;
		this.yya = yya;
		this.zza = zza;
	}

	public static void encode(SyncMovementDirection pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.xxa);
		buf.writeFloat(pkt.yya);
		buf.writeFloat(pkt.zza);
	}

	public static SyncMovementDirection decode(FriendlyByteBuf buf) {
		return new SyncMovementDirection(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	public static void handle(final SyncMovementDirection message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender() != null) {
				ctx.get().getSender().xxa = message.xxa;
				ctx.get().getSender().yya = message.yya;
				ctx.get().getSender().zza = message.zza;
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(LocalPlayer player) {
		Object msg = new SyncMovementDirection(player.xxa, player.yya, player.zza);
		CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
	}
}
