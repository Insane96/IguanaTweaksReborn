package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.network.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
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
}
