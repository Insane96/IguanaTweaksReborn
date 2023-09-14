package insane96mcp.survivalreimagined.module.sleeprespawn.tiredness;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.TirednessSyncMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;

import java.util.Collection;

public class TirednessHandler {
    public static final String TIREDNESS_TAG = SurvivalReimagined.RESOURCE_PREFIX + "tiredness";

    public static float get(LivingEntity entity) {
        return entity.getPersistentData().getFloat(TIREDNESS_TAG);
    }

    public static void set(LivingEntity entity, float tiredness) {
        entity.getPersistentData().putFloat(TIREDNESS_TAG, Math.max(tiredness, 0));
    }

    public static void add(LivingEntity entity, float tiredness) {
        set(entity, get(entity) + tiredness);
    }

    public static void subtract(LivingEntity entity, float tiredness) {
        set(entity, get(entity) - tiredness);
    }

    public static float setAndGet(LivingEntity entity, float tiredness) {
        set(entity, tiredness);
        return get(entity);
    }

    public static float addAndGet(LivingEntity entity, float tiredness) {
        return setAndGet(entity, get(entity) + tiredness);
    }

    public static float subtractAndGet(LivingEntity entity, float tiredness) {
        return setAndGet(entity, get(entity) - tiredness);
    }

    public static void reset(LivingEntity entity) {
        entity.getPersistentData().remove(TIREDNESS_TAG);
    }

    public static float getOnWakeUp(LivingEntity entity) {
        return Mth.clamp(get(entity) - Tiredness.tirednessToEffect.floatValue(), 0, Float.MAX_VALUE);
    }

    public static void syncToClient(ServerPlayer player) {
        Object msg = new TirednessSyncMessage(get(player));
        NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static int setFromCommand(CommandSourceStack source, Collection<ServerPlayer> players, float amount) {
        int set = 0;
        for (ServerPlayer player : players) {
            set(player, amount);
            set++;
            syncToClient(player);
        }
        return set;
    }
}
