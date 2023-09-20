package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.experience.enchanting.SREnchantingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class SyncEnsorcellerStatus {
	BlockPos pos;
	ItemStack item;

	public SyncEnsorcellerStatus(BlockPos pos, ItemStack ingredient) {
		this.pos = pos;
		this.item = ingredient;
	}

	public static void encode(SyncEnsorcellerStatus pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeItem(pkt.item);
	}

	public static SyncEnsorcellerStatus decode(FriendlyByteBuf buf) {
		return new SyncEnsorcellerStatus(buf.readBlockPos(), buf.readItem());
	}

	public static void handle(final SyncEnsorcellerStatus message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (!(Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SREnchantingTableBlockEntity blockEntity))
				return;

			blockEntity.setItem(0, message.item);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, BlockPos pos, SREnchantingTableBlockEntity blockEntity) {
		Object msg = new SyncEnsorcellerStatus(pos, blockEntity.getItem(0));
		level.players().forEach(player -> CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
	}
}
