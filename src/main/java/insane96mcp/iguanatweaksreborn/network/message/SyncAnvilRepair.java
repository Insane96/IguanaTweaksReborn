package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepair;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepairReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncAnvilRepair {

    int count;
    List<AnvilRepair> anvilRepairList;

    public SyncAnvilRepair(List<AnvilRepair> anvilRepairList) {
        this.anvilRepairList = anvilRepairList;
        this.count = anvilRepairList.size();
    }

    public static void encode(SyncAnvilRepair pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.count);
        for (AnvilRepair anvilRepair : pkt.anvilRepairList) {
            anvilRepair.toNetwork(buf);
        }
    }

    public static SyncAnvilRepair decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<AnvilRepair> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(AnvilRepair.fromNetwork(buf));
        }
        return new SyncAnvilRepair(list);
    }

    public static void handle(final SyncAnvilRepair message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AnvilRepairReloadListener.REPAIRS.addAll(message.anvilRepairList));
        ctx.get().setPacketHandled(true);
    }

    public static void sync(List<AnvilRepair> repairs, ServerPlayer player) {
        Object msg = new SyncAnvilRepair(repairs);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
