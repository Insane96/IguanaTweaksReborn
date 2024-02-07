package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatistics;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatsReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemStatisticsApply {

    public ItemStatisticsApply() {
    }

    public static void encode(ItemStatisticsApply pkt, FriendlyByteBuf buf) {
    }

    public static ItemStatisticsApply decode(FriendlyByteBuf buf) {
        return new ItemStatisticsApply();
    }

    public static void handle(final ItemStatisticsApply message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (ItemStatistics itemStatistics : ItemStatsReloadListener.STATS) {
                itemStatistics.applyStats(true);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(ServerPlayer player) {
        Object msg = new ItemStatisticsApply();
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
