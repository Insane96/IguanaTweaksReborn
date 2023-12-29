package insane96mcp.iguanatweaksreborn.utils;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import insane96mcp.insanelib.util.LogHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;

public class Utils {
    public static float getFoodEffectiveness(FoodProperties foodProperties) {
        return foodProperties.getNutrition() + getFoodSaturationRestored(foodProperties);
    }

    public static float getFoodSaturationRestored(FoodProperties foodProperties) {
        return foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 2;
    }

    /**
     * Returns a "synced" random. It's not really synced, it uses level game time, which is usually synced
     */
    public static RandomSource syncedRandom(Player player) {
        RandomSource random = player.getRandom();
        if (player.level().isClientSide)
            random.setSeed(player.level().getGameTime() + 1);
        else
            random.setSeed(player.level().getGameTime());
        random.setSeed(random.nextLong());
        random.setSeed(random.nextLong());
        return random;
    }

    /**
     * Returns the hp regenerated each second
     */
    public static float computeFoodFormula(FoodProperties food, String formula) {
        Expression expression = new Expression(formula);
        try {
            //noinspection ConstantConditions
            EvaluationValue result = expression
                    .with("hunger", food.getNutrition())
                    .and("saturation_modifier", food.getSaturationModifier())
                    .and("effectiveness", getFoodEffectiveness(food))
                    .and("fast_food", food.isFastFood())
                    .evaluate();
            return result.getNumberValue().floatValue();
        }
        catch (Exception ex) {
            LogHelper.error("Failed to evaluate food formula: %s", expression);
            return -1f;
        }
    }
}
