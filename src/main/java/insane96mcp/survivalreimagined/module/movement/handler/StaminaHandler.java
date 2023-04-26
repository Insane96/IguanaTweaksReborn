package insane96mcp.survivalreimagined.module.movement.handler;

import insane96mcp.survivalreimagined.module.movement.feature.Stamina;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class StaminaHandler {
    public static int getMaxStamina(Player player) {
        return Mth.ceil(player.getHealth() * Stamina.staminaPerHalfHeart);
    }

    public static int getStamina(Player player) {
        return player.getPersistentData().getInt(Stamina.STAMINA);
    }

    public static boolean isStaminaLocked(Player player) {
        return player.getPersistentData().getBoolean(Stamina.STAMINA_LOCKED);
    }

    public static boolean canSprint(Player player) {
        return !isStaminaLocked(player) && getStamina(player) > 0 && !player.getAbilities().instabuild;
    }

    public static void setStamina(Player player, int stamina) {
        player.getPersistentData().putInt(Stamina.STAMINA, Mth.clamp(stamina, 0, getMaxStamina(player)));
    }

    public static void consumeStamina(Player player) {
        consumeStamina(player, 1);
    }

    public static void consumeStamina(Player player, int amount) {
        setStamina(player, getStamina(player) - amount);
        if (getStamina(player) <= 0)
            lockSprinting(player);
    }

    public static void regenStamina(Player player) {
        setStamina(player, getStamina(player) + 1);
    }

    public static void lockSprinting(Player player) {
        player.getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, true);
    }

    public static void unlockSprinting(Player player) {
        player.getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, false);
    }
}
