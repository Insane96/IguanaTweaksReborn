package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.mining.blockhardness.BlockHardness;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GlobalHardnessSync {
    float globalMultiplier;

    public GlobalHardnessSync(float globalMultiplier) {
        this.globalMultiplier = globalMultiplier;
    }

    public static void encode(GlobalHardnessSync pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.globalMultiplier);
    }

    public static GlobalHardnessSync decode(FriendlyByteBuf buf) {
        return new GlobalHardnessSync(buf.readFloat());
    }

    public static void handle(final GlobalHardnessSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> BlockHardness.hardnessMultiplier = (double) message.globalMultiplier);
        ctx.get().setPacketHandled(true);
    }

    public static void sync(ServerPlayer player, float globalMultiplier) {
        Object msg = new GlobalHardnessSync(globalMultiplier);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
