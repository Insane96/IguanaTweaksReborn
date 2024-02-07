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

public class ItemStatisticsSync {

    int count;
    List<ItemStatistics> itemStatistics;

    public ItemStatisticsSync(List<ItemStatistics> itemStatistics) {
        this.itemStatistics = itemStatistics;
        this.count = itemStatistics.size();
    }

    public static void encode(ItemStatisticsSync pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.count);
        for (ItemStatistics anvilRepair : pkt.itemStatistics) {
            anvilRepair.toNetwork(buf);
        }
    }

    public static ItemStatisticsSync decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<ItemStatistics> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(ItemStatistics.fromNetwork(buf));
        }
        return new ItemStatisticsSync(list);
    }

    public static void handle(final ItemStatisticsSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ItemStatsReloadListener.STATS.addAll(message.itemStatistics);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(List<ItemStatistics> stats, ServerPlayer player) {
        Object msg = new ItemStatisticsSync(stats);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
