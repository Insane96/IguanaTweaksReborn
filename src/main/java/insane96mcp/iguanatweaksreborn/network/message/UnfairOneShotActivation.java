package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.combat.UnfairOneShot;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnfairOneShotActivation {

	public UnfairOneShotActivation() {
	}

	public static void encode(UnfairOneShotActivation pkt, FriendlyByteBuf buf) {

	}

	public static UnfairOneShotActivation decode(FriendlyByteBuf buf) {
		return new UnfairOneShotActivation();
	}

	public static void handle(final UnfairOneShotActivation message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			UnfairOneShot.activationTicks = 30;
		});
		ctx.get().setPacketHandled(true);
	}

	public static void send(ServerPlayer player) {
		Object msg = new UnfairOneShotActivation();
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
