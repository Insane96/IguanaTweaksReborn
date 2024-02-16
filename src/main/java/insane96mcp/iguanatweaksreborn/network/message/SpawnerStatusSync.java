package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnerStatusSync {
	BlockPos pos;
	boolean status;

	public SpawnerStatusSync(BlockPos pos, boolean status) {
		this.pos = pos;
		this.status = status;
	}

	public static void encode(SpawnerStatusSync pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeBoolean(pkt.status);
	}

	public static SpawnerStatusSync decode(FriendlyByteBuf buf) {
		return new SpawnerStatusSync(buf.readBlockPos(), buf.readBoolean());
	}

	public static void handle(final SpawnerStatusSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level().getBlockEntity(message.pos) instanceof SpawnerBlockEntity spawnerBlockEntity) {
				spawnerBlockEntity.getCapability(SpawnerData.INSTANCE).ifPresent(iSpawner -> iSpawner.setDisabled(message.status));
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
