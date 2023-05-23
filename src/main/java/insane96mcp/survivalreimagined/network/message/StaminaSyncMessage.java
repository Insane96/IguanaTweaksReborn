package insane96mcp.survivalreimagined.network.message;

import insane96mcp.survivalreimagined.module.movement.feature.Stamina;
import insane96mcp.survivalreimagined.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StaminaSyncMessage {

    int stamina;
    boolean staminaLocked;

    public StaminaSyncMessage(int stamina, boolean staminaLocked) {
        this.stamina = stamina;
        this.staminaLocked = staminaLocked;
    }

    public static void encode(StaminaSyncMessage pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.stamina);
        buf.writeBoolean(pkt.staminaLocked);
    }

    public static StaminaSyncMessage decode(FriendlyByteBuf buf) {
        return new StaminaSyncMessage(buf.readInt(), buf.readBoolean());
    }

    public static void handle(final StaminaSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkHelper.getSidedPlayer(ctx.get()).getPersistentData().putInt(Stamina.STAMINA, message.stamina);
            NetworkHelper.getSidedPlayer(ctx.get()).getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, message.staminaLocked);
        });
        ctx.get().setPacketHandled(true);
    }
}
