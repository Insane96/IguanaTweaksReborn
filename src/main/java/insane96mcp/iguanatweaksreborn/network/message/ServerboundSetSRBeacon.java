package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.misc.beaconconduit.SRBeaconMenu;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

import static insane96mcp.iguanatweaksreborn.network.NetworkHandler.CHANNEL;

public class ServerboundSetSRBeacon {
    final Optional<MobEffect> mobEffect;
    final int amplifier;

    public ServerboundSetSRBeacon(Optional<MobEffect> mobEffect, int amplifier) {
        this.mobEffect = mobEffect;
        this.amplifier = amplifier;
    }

    public static void encode(ServerboundSetSRBeacon pkt, FriendlyByteBuf buf) {
        buf.writeOptional(pkt.mobEffect, (byteBuf, mobEffect) -> byteBuf.writeId(BuiltInRegistries.MOB_EFFECT, mobEffect));
        buf.writeInt(pkt.amplifier);
    }

    public static ServerboundSetSRBeacon decode(FriendlyByteBuf buf) {
        return new ServerboundSetSRBeacon(buf.readOptional((byteBuf) -> byteBuf.readById(BuiltInRegistries.MOB_EFFECT)), buf.readInt());
    }

    public static void handle(final ServerboundSetSRBeacon message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null)
                return;

            ServerPlayer player = ctx.get().getSender();
            AbstractContainerMenu abstractcontainermenu = player.containerMenu;
            if (abstractcontainermenu instanceof SRBeaconMenu srBeaconMenu) {
                if (!player.containerMenu.stillValid(player)) {
                    LogHelper.debug("Player {} interacted with invalid menu {}", player, player.containerMenu);
                    return;
                }

                srBeaconMenu.updateEffect(message.mobEffect, message.amplifier);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void updateServer(LocalPlayer player, MobEffect mobEffect, int amplifier) {
        Object msg = new ServerboundSetSRBeacon(Optional.of(mobEffect), amplifier);
        CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_SERVER);
    }
}
