package insane96mcp.survivalreimagined.network;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ElectrocutionParticleMessage {
    IntList ids;

    public ElectrocutionParticleMessage() {}

    public ElectrocutionParticleMessage(IntList ids) {
        this.ids = ids;
    }

    public void addId(int id) {
        this.ids.add(id);
    }

    public static void encode(ElectrocutionParticleMessage pkt, FriendlyByteBuf buf) {
        buf.writeIntIdList(pkt.ids);
    }

    public static ElectrocutionParticleMessage decode(FriendlyByteBuf buf) {
        return new ElectrocutionParticleMessage(buf.readIntIdList());
    }

    public static void handle(final ElectrocutionParticleMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientNetworkHandler.summonSpark(message.ids));
        ctx.get().setPacketHandled(true);
    }
}
