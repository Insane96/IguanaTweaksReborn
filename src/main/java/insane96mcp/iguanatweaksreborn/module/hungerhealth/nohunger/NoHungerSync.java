package insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger;

import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.insanelib.base.Module;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NoHungerSync {
    final boolean noHunger;

    public NoHungerSync(boolean noHunger) {
        this.noHunger = noHunger;
    }

    public static void encode(NoHungerSync pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.noHunger);
    }

    public static NoHungerSync decode(FriendlyByteBuf buf) {
        return new NoHungerSync(buf.readBoolean());
    }

    public static void handle(final NoHungerSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Module.getFeature(NoHunger.class).setEnabledConfig(message.noHunger));
        ctx.get().setPacketHandled(true);
    }

    public static void sync(boolean noHunger, ServerPlayer player) {
        Object msg = new NoHungerSync(noHunger);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
