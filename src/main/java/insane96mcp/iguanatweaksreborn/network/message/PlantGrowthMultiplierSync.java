package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.PlantGrowthMultiplier;
import insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth.PlantsGrowthReloadListener;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PlantGrowthMultiplierSync {

    int count;
    List<PlantGrowthMultiplier> plantGrowthMultipliers;

    public PlantGrowthMultiplierSync(List<PlantGrowthMultiplier> plantGrowthMultipliers) {
        this.plantGrowthMultipliers = plantGrowthMultipliers;
        this.count = plantGrowthMultipliers.size();
    }

    public static void encode(PlantGrowthMultiplierSync pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.count);
        /*for (PlantGrowthMultiplier plantGrowthMultiplier : pkt.plantGrowthMultipliers) {
            plantGrowthMultiplier.toNetwork(buf);
        }*/
    }

    public static PlantGrowthMultiplierSync decode(FriendlyByteBuf buf) {
        int count = buf.readInt();
        List<PlantGrowthMultiplier> list = new ArrayList<>(count);
        /*for (int i = 0; i < count; i++) {
            list.add(PlantGrowthMultiplier.fromNetwork(buf));
        }*/
        return new PlantGrowthMultiplierSync(list);
    }

    public static void handle(final PlantGrowthMultiplierSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> PlantsGrowthReloadListener.GROWTH_MULTIPLIERS.addAll(message.plantGrowthMultipliers));
        ctx.get().setPacketHandled(true);
    }

    public static void sync(List<PlantGrowthMultiplier> multipliers, ServerPlayer player) {
        Object msg = new PlantGrowthMultiplierSync(multipliers);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
