package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BreakWithNoSound {
	BlockPos pos;
	int state;

	public BreakWithNoSound(BlockPos pos, int state) {
		this.pos = pos;
		this.state = state;
	}

	public static void encode(BreakWithNoSound pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeInt(pkt.state);
	}

	public static BreakWithNoSound decode(FriendlyByteBuf buf) {
		return new BreakWithNoSound(buf.readBlockPos(), buf.readInt());
	}

	public static void handle(final BreakWithNoSound message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level != null)
                Minecraft.getInstance().level.addDestroyBlockEffect(message.pos, Block.stateById(message.state));
        });
		ctx.get().setPacketHandled(true);
	}

	public static void send(ServerPlayer player, BlockPos pos, BlockState state) {
		Object msg = new BreakWithNoSound(pos, Block.getId(state));
		NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
}
