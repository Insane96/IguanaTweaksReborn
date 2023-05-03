package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.farming.feature.HarderCrops;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class HarderCropsSync {
	float hardness;
	boolean onlyFullyGrown;

	public HarderCropsSync(float hardness, boolean onlyFullyGrown) {
		this.hardness = hardness;
		this.onlyFullyGrown = onlyFullyGrown;
	}

	public static void encode(HarderCropsSync pkt, FriendlyByteBuf buf) {
		buf.writeFloat(pkt.hardness);
		buf.writeBoolean(pkt.onlyFullyGrown);
	}

	public static HarderCropsSync decode(FriendlyByteBuf buf) {
		return new HarderCropsSync(buf.readFloat(), buf.readBoolean());
	}

	public static void handle(final HarderCropsSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> HarderCrops.applyHardness(message.hardness, message.onlyFullyGrown));
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerPlayer player, float hardness, boolean onlyFullyGrown) {
		Object msg = new HarderCropsSync(hardness, onlyFullyGrown);
		CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
