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

public class InvulnerableTimeMessageSync {
	int entityId;
	int invulnerableTime;

	public InvulnerableTimeMessageSync(int entityId, int invulnerableTime) {
		this.entityId = entityId;
		this.invulnerableTime = invulnerableTime;
	}

	public static void encode(InvulnerableTimeMessageSync pkt, FriendlyByteBuf buf) {
		buf.writeInt(pkt.entityId);
		buf.writeInt(pkt.invulnerableTime);
	}

	public static InvulnerableTimeMessageSync decode(FriendlyByteBuf buf) {
		return new InvulnerableTimeMessageSync(buf.readInt(), buf.readInt());
	}

	public static void handle(final InvulnerableTimeMessageSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ClientNetworkHandler.handleSyncInvulnerableTimeMessage(message.entityId, message.invulnerableTime));
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, Entity entity, int invincibilityFrames) {
		Object msg = new InvulnerableTimeMessageSync(entity.getId(), invincibilityFrames);
		for (Player player : level.players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
