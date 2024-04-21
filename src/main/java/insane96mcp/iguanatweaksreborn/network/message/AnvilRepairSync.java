package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepair;
import insane96mcp.iguanatweaksreborn.module.experience.anvils.AnvilRepairReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AnvilRepairSync {

    int count;
    Map<ResourceLocation, AnvilRepair> anvilRepairList;

    public AnvilRepairSync(Map<ResourceLocation, AnvilRepair> anvilRepairList) {
        this.anvilRepairList = anvilRepairList;
        this.count = anvilRepairList.size();
    }

    public static void encode(AnvilRepairSync pkt, FriendlyByteBuf buf) {
        buf.writeMap(pkt.anvilRepairList, FriendlyByteBuf::writeResourceLocation, (b, anvilRepair) -> anvilRepair.toNetwork(buf));
    }

    public static AnvilRepairSync decode(FriendlyByteBuf buf) {
        return new AnvilRepairSync(buf.readMap(FriendlyByteBuf::readResourceLocation, AnvilRepair::fromNetwork));
    }

    public static void handle(final AnvilRepairSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            AnvilRepairReloadListener.REPAIRS.clear();
            AnvilRepairReloadListener.REPAIRS.putAll(message.anvilRepairList);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(HashMap<ResourceLocation, AnvilRepair> repairs, ServerPlayer player) {
        Object msg = new AnvilRepairSync(repairs);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
