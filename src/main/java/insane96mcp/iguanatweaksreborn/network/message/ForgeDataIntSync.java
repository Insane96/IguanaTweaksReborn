package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ForgeDataIntSync {

    final int entityId;
    final String tag;
    final int value;

    public ForgeDataIntSync(int entityId, String tag, int value) {
        this.entityId = entityId;
        this.tag = tag;
        this.value = value;
    }

    public static void encode(ForgeDataIntSync pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.entityId);
        buf.writeUtf(pkt.tag);
        buf.writeInt(pkt.value);
    }

    public static ForgeDataIntSync decode(FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        String tag = buf.readUtf();
        int data = buf.readInt();
        return new ForgeDataIntSync(entityId, tag, data);
    }

    public static void handle(final ForgeDataIntSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(message.entityId);
            if (!(entity instanceof LivingEntity livingEntity))
                return;

            livingEntity.getPersistentData().putInt(message.tag, message.value);
        });
        ctx.get().setPacketHandled(true);
    }

    public static void sync(LivingEntity entity, String tag, int value) {
        Object msg = new ForgeDataIntSync(entity.getId(), tag, value);
        ((ServerLevel)entity.level()).players().forEach(player -> NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sync(ServerPlayer player, LivingEntity entity, String tag, int value) {
        Object msg = new ForgeDataIntSync(entity.getId(), tag, value);
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
