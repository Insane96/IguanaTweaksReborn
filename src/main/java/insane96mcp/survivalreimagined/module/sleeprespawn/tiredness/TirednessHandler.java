package insane96mcp.survivalreimagined.module.sleeprespawn.tiredness;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.world.entity.LivingEntity;

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
}
