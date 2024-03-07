package insane96mcp.iguanatweaksreborn.module.world.spawners;

import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerData;
import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerDataImpl;
import insane96mcp.iguanatweaksreborn.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnerStatusSync {
	BlockPos pos;
	SpawnerDataImpl spawnerData;
	int delay;
	int requiredPlayerRange;

	public SpawnerStatusSync(BlockPos pos, SpawnerDataImpl spawnerData, int delay, int requiredPlayerRange) {
		this.pos = pos;
		this.spawnerData = spawnerData;
		this.delay = delay;
		this.requiredPlayerRange = requiredPlayerRange;
	}

	public static void encode(SpawnerStatusSync pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		pkt.spawnerData.toNetwork(buf);
		buf.writeInt(pkt.delay);
		buf.writeInt(pkt.requiredPlayerRange);
	}

	public static SpawnerStatusSync decode(FriendlyByteBuf buf) {
		return new SpawnerStatusSync(buf.readBlockPos(), SpawnerDataImpl.fromNetwork(buf), buf.readInt(), buf.readInt());
	}

	public static void handle(final SpawnerStatusSync message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (NetworkHelper.getSidedPlayer(ctx.get()).level().getBlockEntity(message.pos) instanceof SpawnerBlockEntity spawnerBlockEntity) {
				spawnerBlockEntity.getCapability(SpawnerData.INSTANCE).ifPresent(iSpawner -> {
					iSpawner.setSpawnedMobs(message.spawnerData.getSpawnedMobs());
					iSpawner.setDisabled(message.spawnerData.isDisabled());
					iSpawner.setEmpowered(message.spawnerData.isEmpowered());
				});
				spawnerBlockEntity.getSpawner().spawnDelay = message.delay;
				spawnerBlockEntity.getSpawner().requiredPlayerRange = message.delay;
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
