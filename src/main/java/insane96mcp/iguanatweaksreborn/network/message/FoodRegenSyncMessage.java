package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.NoHunger;
import insane96mcp.iguanatweaksreborn.network.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FoodRegenSyncMessage {

    float regenStrength;

    public FoodRegenSyncMessage(float regenStrength) {
        this.regenStrength = regenStrength;
    }

    public static void encode(FoodRegenSyncMessage pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.regenStrength);
    }

    public static FoodRegenSyncMessage decode(FriendlyByteBuf buf) {
        return new FoodRegenSyncMessage(buf.readFloat());
    }

    public static void handle(final FoodRegenSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NoHunger.setFoodRegenStrength(NetworkHelper.getSidedPlayer(ctx.get()), message.regenStrength);
        });
        ctx.get().setPacketHandled(true);
    }
}
