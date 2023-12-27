package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatistics;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatsReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncItemStatistics {

    int count;
    List<ItemStatistics> itemStatistics;

    public SyncItemStatistics(List<ItemStatistics> itemStatistics) {
        this.itemStatistics = itemStatistics;
        this.count = itemStatistics.size();
    }

    public static void encode(SyncItemStatistics pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.count);
        for (ItemStatistics anvilRepair : pkt.itemStatistics) {
            anvilRepair.toNetwork(buf);
        }
    }

    public static SyncItemStatistics decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<ItemStatistics> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(ItemStatistics.fromNetwork(buf));
        }
        return new SyncItemStatistics(list);
    }

    public static void handle(final SyncItemStatistics message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStatsReloadListener.STATS.addAll(message.itemStatistics);
            for (ItemStatistics itemStatistics : ItemStatsReloadListener.STATS) {
                itemStatistics.applyStats(true);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(List<ItemStatistics> stats, ServerPlayer player) {
        Object msg = new SyncItemStatistics(stats);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
