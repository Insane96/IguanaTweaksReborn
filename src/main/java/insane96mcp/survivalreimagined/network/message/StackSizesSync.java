package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.items.StackSizes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class StackSizesSync {
	Boolean foodStackReduction;
	String foodStackReductionFormula;
	Integer stackableSoups;
	Double itemStackMultiplier;
	Double blockStackMultiplier;

	public StackSizesSync(Boolean foodStackReduction, String foodStackReductionFormula, Integer stackableSoups, Double itemStackMultiplier, Double blockStackMultiplier) {
		this.foodStackReduction = foodStackReduction;
		this.foodStackReductionFormula = foodStackReductionFormula;
		this.stackableSoups = stackableSoups;
		this.itemStackMultiplier = itemStackMultiplier;
		this.blockStackMultiplier = blockStackMultiplier;
	}

	public static void encode(StackSizesSync pkt, FriendlyByteBuf buf) {
		buf.writeBoolean(pkt.foodStackReduction);
		buf.writeUtf(pkt.foodStackReductionFormula);
		buf.writeInt(pkt.stackableSoups);
		buf.writeDouble(pkt.itemStackMultiplier);
		buf.writeDouble(pkt.blockStackMultiplier);
	}

	public static StackSizesSync decode(FriendlyByteBuf buf) {
		return new StackSizesSync(buf.readBoolean(), buf.readUtf(), buf.readInt(), buf.readDouble(), buf.readDouble());
	}

	public static void handle(final StackSizesSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			StackSizes.foodStackReduction = message.foodStackReduction;
			StackSizes.foodStackReductionFormula = message.foodStackReductionFormula;
			StackSizes.stackableSoups = message.stackableSoups;
			StackSizes.itemStackMultiplier = message.itemStackMultiplier;
			StackSizes.blockStackMultiplier = message.blockStackMultiplier;
			StackSizes.processStackSizes(true);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(Boolean foodStackReduction, String foodStackReductionFormula, Integer stackableSoups, Double itemStackMultiplier, Double blockStackMultiplier, ServerPlayer player) {
		Object msg = new StackSizesSync(foodStackReduction, foodStackReductionFormula, stackableSoups, itemStackMultiplier, blockStackMultiplier);
		CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
