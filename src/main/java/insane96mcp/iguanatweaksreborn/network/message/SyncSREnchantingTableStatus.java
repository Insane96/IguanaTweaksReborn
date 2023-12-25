package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.experience.enchanting.SREnchantingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.iguanatweaksreborn.network.NetworkHandler.CHANNEL;

public class SyncSREnchantingTableStatus {
	BlockPos pos;
	ItemStack item;

	public SyncSREnchantingTableStatus(BlockPos pos, ItemStack ingredient) {
		this.pos = pos;
		this.item = ingredient;
	}

	public static void encode(SyncSREnchantingTableStatus pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeItem(pkt.item);
	}

	public static SyncSREnchantingTableStatus decode(FriendlyByteBuf buf) {
		return new SyncSREnchantingTableStatus(buf.readBlockPos(), buf.readItem());
	}

	public static void handle(final SyncSREnchantingTableStatus message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (!(Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof SREnchantingTableBlockEntity blockEntity))
				return;

			blockEntity.setItem(0, message.item);
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, BlockPos pos, SREnchantingTableBlockEntity blockEntity) {
		Object msg = new SyncSREnchantingTableStatus(pos, blockEntity.getItem(0));
		level.players().forEach(player -> CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
	}
}
