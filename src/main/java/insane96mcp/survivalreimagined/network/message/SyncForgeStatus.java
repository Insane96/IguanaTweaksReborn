package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.survivalreimagined.network.NetworkHandler.CHANNEL;

public class SyncForgeStatus {
	BlockPos pos;
	ItemStack ingredient;
	ItemStack gear;
	ItemStack output;
	int smashes;
	int smashesRequired;

	public SyncForgeStatus(BlockPos pos, ItemStack ingredient, ItemStack gear, ItemStack output, int smashes, int smashesRequired) {
		this.pos = pos;
		this.ingredient = ingredient;
		this.gear = gear;
		this.output = output;
		this.smashes = smashes;
		this.smashesRequired = smashesRequired;
	}

	public static void encode(SyncForgeStatus pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeItem(pkt.ingredient);
		buf.writeItem(pkt.gear);
		buf.writeItem(pkt.output);
		buf.writeInt(pkt.smashes);
		buf.writeInt(pkt.smashesRequired);
	}

	public static SyncForgeStatus decode(FriendlyByteBuf buf) {
		return new SyncForgeStatus(buf.readBlockPos(), buf.readItem(), buf.readItem(), buf.readItem(), buf.readInt(), buf.readInt());
	}

	public static void handle(final SyncForgeStatus message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (!(Minecraft.getInstance().level.getBlockEntity(message.pos) instanceof ForgeBlockEntity blockEntity))
				return;

			blockEntity.setItem(0, message.ingredient);
			blockEntity.setItem(1, message.gear);
			blockEntity.setItem(2, message.output);
			blockEntity.smashes = message.smashes;
			blockEntity.smashesRequired = message.smashesRequired;
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(ServerLevel level, BlockPos pos, ForgeBlockEntity blockEntity) {
		Object msg = new SyncForgeStatus(pos, blockEntity.getItem(0), blockEntity.getItem(1), blockEntity.getItem(2), blockEntity.smashes, blockEntity.smashesRequired);
		level.players().forEach(player -> {
			CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		});
	}
}
