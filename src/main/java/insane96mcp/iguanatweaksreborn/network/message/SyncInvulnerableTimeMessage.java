package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.network.ClientNetworkHandler;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncInvulnerableTimeMessage {
	int entityId;
	int invulnerableTime;

	public SyncInvulnerableTimeMessage(int entityId, int invulnerableTime) {
		this.entityId = entityId;
		this.invulnerableTime = invulnerableTime;
	}

	public static void encode(SyncInvulnerableTimeMessage pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.entityId);
		buf.writeInt(pkt.invulnerableTime);
	}

	public static SyncInvulnerableTimeMessage decode(FriendlyByteBuf buf) {
		return new SyncInvulnerableTimeMessage(buf.readInt(), buf.readInt());
	}

	public static void handle(final SyncInvulnerableTimeMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientNetworkHandler.handleSyncInvulnerableTimeMessage(message.entityId, message.invulnerableTime));
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, Entity entity, int invincibilityFrames) {
		Object msg = new SyncInvulnerableTimeMessage(entity.getId(), invincibilityFrames);
		for (Player player : level.players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
