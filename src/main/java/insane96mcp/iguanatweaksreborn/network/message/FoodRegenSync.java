package insane96mcp.iguanatweaksreborn.network.message;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.NoHunger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FoodRegenSync {

    float regenStrength;

    public FoodRegenSync(float regenStrength) {
        this.regenStrength = regenStrength;
    }

    public static void encode(FoodRegenSync pkt, FriendlyByteBuf buf) {
        buf.writeFloat(pkt.regenStrength);
    }

    public static FoodRegenSync decode(FriendlyByteBuf buf) {
        return new FoodRegenSync(buf.readFloat());
    }

    public static void handle(final FoodRegenSync message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NoHunger.setFoodRegenStrength(Minecraft.getInstance().player, message.regenStrength);
        });
        ctx.get().setPacketHandled(true);
    }
}
