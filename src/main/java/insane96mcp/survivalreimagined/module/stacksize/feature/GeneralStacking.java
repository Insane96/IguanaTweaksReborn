package insane96mcp.survivalreimagined.module.stacksize.feature;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Utils;
import insane96mcp.survivalreimagined.utils.Weights;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Label(name = "General Stacking", description = "Make food, items and blocks less stackable. Items and Blocks are disabled by default. Changing stuff might require a Minecraft restart.")
@LoadFeature(module = Modules.Ids.STACK_SIZE)
public class GeneralStacking extends SRFeature {
    public static final ResourceLocation NO_STACK_SIZE_CHANGES = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_stack_size_changes");

    @Config
    @Label(name = "Food Stack Reduction", description = "Food stack sizes will be reduced based off their hunger restored and saturation multiplier. See 'Food Stack Reduction Formula' for the formula")
    public static Boolean foodStackReduction = true;
    @Config
    @Label(name = "Food Stack Reduction Formula", description = "The formula to calculate the stack size of a food item. Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html.")
    public static String foodStackReductionFormula = "ROUND((1 - (effectiveness - 1) / 24) * 64 * 0.2, 0)";
    @Config(min = 1, max = 64)
    @Label(name = "Stackable Stews", description = "Stews will stack up to this number. It's overridden by 'foodStackReduction' if enabled. Still affected by black/whitelist")
    public static Integer stackableSoups = 16;
    @Config(min = 0.01d, max = 64d)
    @Label(name = "Item Stack Multiplier", description = "Items max stack sizes (excluding blocks) will be multiplied by this value. Foods will be overridden by 'Food Stack Reduction' or 'Food Stack Multiplier' if are active. Setting to 1 will disable this feature.")
    public static Double itemStackMultiplier = 1d;
    @Config
    @Label(name = "Block Stack Reduction", description = "Blocks max stack sizes will be reduced based off their material.")
    public static Boolean blockStackReduction = false;
    @Config(min = 0.01d, max = 64d)
    @Label(name = "Block Stack Multiplier", description = "All the blocks max stack sizes will be multiplied by this value to increase / decrease them. This is applied after the reduction from 'Block Stack Reduction'.")
    public static Double blockStackMultiplier = 1.0d;
    @Config
    @Label(name = "Block Stack Affected by Material", description = "When true, block stacks are affected by both their material type and the block stack multiplier. If false, block stacks will be affected by the multiplier only.")
    public static Boolean blockStackAffectedByMaterial = true;

	public GeneralStacking(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadJsonConfigs() {
        synchronized (mutex) {
            //resetStackSizes();
            if (originalStackSizes.isEmpty()) {
                for (Item item : ForgeRegistries.ITEMS.getValues()) {
                    originalStackSizes.put(item, item.maxStackSize);
                }
            }
            processItemStackSizes();
            processBlockStackSizes();
            processStewStackSizes();
            processFoodStackSizes();
        }
    }

    @Override
    public void readConfig(final ModConfigEvent event) {
        super.readConfig(event);
    }

    private final Object mutex = new Object();

    HashMap<Item, Integer> originalStackSizes = new HashMap<>();
    public void resetStackSizes() {
        if (originalStackSizes.isEmpty()) {
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                originalStackSizes.put(item, item.maxStackSize);
            }
        }
        else {
            for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
                entry.getKey().maxStackSize = entry.getValue();
            }
        }
    }

    //Items
    public void processItemStackSizes() {
        if (!this.isEnabled()
                || itemStackMultiplier == 1d)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (item instanceof BlockItem
                    || item.maxStackSize == 1
                    || isItemInTag(item, NO_STACK_SIZE_CHANGES))
                continue;

            double stackSize = entry.getValue() * itemStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Blocks
    public void processBlockStackSizes() {
        if (!this.isEnabled()
                || !blockStackReduction)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (!(item instanceof BlockItem)
                    || item.maxStackSize == 1
                    || isItemInTag(item, NO_STACK_SIZE_CHANGES))
                continue;

            Block block = ((BlockItem) item).getBlock();
            double weight = blockStackAffectedByMaterial ? Weights.getWeightForState(block.defaultBlockState()) : 1d;
            double stackSize = (entry.getValue() / weight) * blockStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }

    }

    //Stews
    public void processStewStackSizes() {
        if (!this.isEnabled()
                || stackableSoups == 1)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (!(item instanceof BowlFoodItem) && !(item instanceof SuspiciousStewItem)
                    || isItemInTag(item, NO_STACK_SIZE_CHANGES))
                continue;

            item.maxStackSize = Math.round(stackableSoups);
        }

    }

    //Food
    @SuppressWarnings("deprecation")
    public void processFoodStackSizes() {
        if (!this.isEnabled()
                || !foodStackReduction)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (!item.isEdible()
                    || isItemInTag(item, NO_STACK_SIZE_CHANGES))
                continue;

            FoodProperties food = item.getFoodProperties();
            Expression expression = new Expression(foodStackReductionFormula);
            try {
                //noinspection ConstantConditions Can't be null as I check for Item#isEdible
                EvaluationValue result = expression
                        .with("hunger", food.getNutrition())
                        .and("saturation_modifier", food.getSaturationModifier())
                        .and("effectiveness", Utils.getFoodEffectiveness(food))
                        .evaluate();
                int stackSize = Mth.clamp(result.getNumberValue().intValue(), 1, 64);
                item.maxStackSize = Math.round(stackSize);
            }
            catch (Exception ex) {
                LogHelper.error("Failed to parse or evaluate food stack size formula: %s", expression);
            }
        }
    }

    /**
     * Fixes soups, potions, etc. consuming that don't work properly when stacked
     */
    @SubscribeEvent
    public void fixUnstackableItemsEat(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack original = event.getItem();
            ItemStack result = event.getResultStack();
            if (original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET || result.getItem() == Items.GLASS_BOTTLE)) {
                ItemStack newResult = original.copy();
                newResult.setCount(original.getCount() - 1);
                event.setResultStack(newResult);
                player.addItem(result);
            }
        }
    }
}