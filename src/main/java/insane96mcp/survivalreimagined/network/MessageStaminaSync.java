package insane96mcp.survivalreimagined.network;

import insane96mcp.survivalreimagined.module.movement.feature.Stamina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStaminaSync {

    int stamina;
    boolean staminaLocked;

    public MessageStaminaSync(int stamina, boolean staminaLocked) {
        this.stamina = stamina;
        this.staminaLocked = staminaLocked;
    }

    public static void encode(MessageStaminaSync pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.stamina);
        buf.writeBoolean(pkt.staminaLocked);
    }

    public static MessageStaminaSync decode(FriendlyByteBuf buf) {
        return new MessageStaminaSync(buf.readInt(), buf.readBoolean());
    }

    public static void handle(final MessageStaminaSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkHelper.getSidedPlayer(ctx.get()).getPersistentData().putInt(Stamina.STAMINA, message.stamina);
            NetworkHelper.getSidedPlayer(ctx.get()).getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, message.staminaLocked);
        });
        ctx.get().setPacketHandled(true);
    }
}
