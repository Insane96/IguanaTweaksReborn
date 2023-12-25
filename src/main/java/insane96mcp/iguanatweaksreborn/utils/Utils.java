package insane96mcp.iguanatweaksreborn.utils;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
    public static boolean isItemInTag(Item item, ResourceLocation tag) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(item);
    }

    public static boolean isBlockInTag(Block block, ResourceLocation tag) {
        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.BLOCKS.tags().getTag(tagKey).contains(block);
    }

    public static boolean isEntityInTag(Entity entity, ResourceLocation tag) {
        return isEntityTypeInTag(entity.getType(), tag);
    }

    public static boolean isEntityTypeInTag(EntityType<?> entityType, ResourceLocation tag) {
        TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tag);
        //noinspection ConstantConditions
        return ForgeRegistries.ENTITY_TYPES.tags().getTag(tagKey).contains(entityType);
    }

    public static boolean isItemInTag(Item item, TagKey<Item> tag) {
        //noinspection ConstantConditions
        return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
    }

    public static boolean isBlockInTag(Block block, TagKey<Block> tag) {
        //noinspection ConstantConditions
        return ForgeRegistries.BLOCKS.tags().getTag(tag).contains(block);
    }

    public static boolean isEntityInTag(Entity entity, TagKey<EntityType<?>> tag) {
        return isEntityTypeInTag(entity.getType(), tag);
    }

    public static boolean isEntityTypeInTag(EntityType<?> entityType, TagKey<EntityType<?>> tag) {
        //noinspection ConstantConditions
        return ForgeRegistries.ENTITY_TYPES.tags().getTag(tag).contains(entityType);
    }

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
