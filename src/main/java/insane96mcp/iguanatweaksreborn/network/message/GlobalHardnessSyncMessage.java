package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.mining.blockhardness.BlockHardness;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static insane96mcp.iguanatweaksreborn.network.NetworkHandler.CHANNEL;

public class GlobalHardnessSyncMessage {
    float globalMultiplier;

    public GlobalHardnessSyncMessage(float globalMultiplier) {
        this.globalMultiplier = globalMultiplier;
    }

    public static void encode(GlobalHardnessSyncMessage pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.globalMultiplier);
    }

    public static GlobalHardnessSyncMessage decode(FriendlyByteBuf buf) {
        return new GlobalHardnessSyncMessage(buf.readFloat());
    }

    public static void handle(final GlobalHardnessSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> BlockHardness.hardnessMultiplier = (double) message.globalMultiplier);
        ctx.get().setPacketHandled(true);
    }

    public static void sync(ServerPlayer player, float globalMultiplier) {
        Object msg = new GlobalHardnessSyncMessage(globalMultiplier);
        CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
