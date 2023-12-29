package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MovementDirectionSync {
	float xxa;
	float yya;
	float zza;

	public MovementDirectionSync(float xxa, float yya, float zza) {
		this.xxa = xxa;
		this.yya = yya;
		this.zza = zza;
	}

	public static void encode(MovementDirectionSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.xxa);
		buf.writeFloat(pkt.yya);
		buf.writeFloat(pkt.zza);
	}

	public static MovementDirectionSync decode(FriendlyByteBuf buf) {
		return new MovementDirectionSync(buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	public static void handle(final MovementDirectionSync message, Supplier<NetworkEvent.Context> ctx) {
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
		Object msg = new MovementDirectionSync(player.xxa, player.yya, player.zza);
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
	}
}
