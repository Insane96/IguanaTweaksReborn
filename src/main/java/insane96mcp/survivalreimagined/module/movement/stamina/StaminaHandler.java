package insane96mcp.survivalreimagined.module.movement.stamina;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class StaminaHandler {
    public static float getMaxStamina(Player player) {
        return Mth.ceil(player.getHealth() * Stamina.staminaPerHalfHeart);
    }

    public static float getStamina(Player player) {
        return player.getPersistentData().getFloat(Stamina.STAMINA);
    }

    public static boolean isStaminaLocked(Player player) {
        return player.getPersistentData().getBoolean(Stamina.STAMINA_LOCKED);
    }

    public static boolean canSprint(Player player) {
        return !isStaminaLocked(player) && getStamina(player) > 0 && !player.getAbilities().instabuild;
    }

    public static float setStamina(Player player, float stamina) {
        stamina = Mth.clamp(stamina, 0, getMaxStamina(player));
        player.getPersistentData().putFloat(Stamina.STAMINA, stamina);
        return stamina;
    }

    public static void consumeStamina(Player player, float amount) {
        setStamina(player, getStamina(player) - amount);
        if (getStamina(player) <= 0)
            lockSprinting(player);
    }

    public static float regenStamina(Player player, float amount) {
        return setStamina(player, getStamina(player) + amount);
    }

    public static void lockSprinting(Player player) {
        player.getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, true);
    }

    public static void unlockSprinting(Player player) {
        player.getPersistentData().putBoolean(Stamina.STAMINA_LOCKED, false);
    }
}
