package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.movement.BackwardsSlowdown;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BackwardsSlowdownUpdate {
	float zza;

	public BackwardsSlowdownUpdate(float zza) {
		this.zza = zza;
	}

	public static void encode(BackwardsSlowdownUpdate pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.zza);
	}

	public static BackwardsSlowdownUpdate decode(FriendlyByteBuf buf) {
		return new BackwardsSlowdownUpdate(buf.readFloat());
	}

	public static void handle(final BackwardsSlowdownUpdate message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender() != null)
				BackwardsSlowdown.applyModifier(ctx.get().getSender(), message.zza);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(LocalPlayer player) {
		Object msg = new BackwardsSlowdownUpdate(player.zza);
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
	}
}
