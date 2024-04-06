package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.mining.blockdata.BlockData;
import insane96mcp.iguanatweaksreborn.module.mining.blockdata.BlockDataReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlockDataSync {

    int count;
    List<BlockData> blockDataList;

    public BlockDataSync(List<BlockData> blockDataList) {
        this.blockDataList = blockDataList;
        this.count = blockDataList.size();
    }

    public static void encode(BlockDataSync pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.count);
        for (BlockData anvilRepair : pkt.blockDataList) {
            anvilRepair.toNetwork(buf);
        }
    }

    public static BlockDataSync decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<BlockData> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(BlockData.fromNetwork(buf));
        }
        return new BlockDataSync(list);
    }

    public static void handle(final BlockDataSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockDataReloadListener.restoreOriginalDataAndClear();
            for (BlockData data : message.blockDataList) {
                data.apply(false);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(List<BlockData> data, ServerPlayer player) {
        Object msg = new BlockDataSync(data);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
