package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.world.spawners.capability.SpawnerData;
import insane96mcp.survivalreimagined.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnerStatusSyncMessage {
	BlockPos pos;
	boolean status;

	public SpawnerStatusSyncMessage(BlockPos pos, boolean status) {
		this.pos = pos;
		this.status = status;
	}

	public static void encode(SpawnerStatusSyncMessage pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeBoolean(pkt.status);
	}

	public static SpawnerStatusSyncMessage decode(FriendlyByteBuf buf) {
		return new SpawnerStatusSyncMessage(buf.readBlockPos(), buf.readBoolean());
	}

	public static void handle(final SpawnerStatusSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (NetworkHelper.getSidedPlayer(ctx.get()).level().getBlockEntity(message.pos) instanceof SpawnerBlockEntity spawnerBlockEntity) {
				spawnerBlockEntity.getCapability(SpawnerData.INSTANCE).ifPresent(iSpawner -> iSpawner.setDisabled(message.status));
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
