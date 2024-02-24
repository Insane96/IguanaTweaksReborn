package insane96mcp.iguanatweaksreborn.module.items;

import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.message.StackSizesSync;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Label(name = "Stack Sizes", description = "Make food, items and blocks less stackable. Items and Blocks are disabled by default. Changing stuff might require a Minecraft restart.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class StackSizes extends Feature {
    public static final TagKey<Item> NO_STACK_SIZE_CHANGES = ITRItemTagsProvider.create("no_stack_size_changes");

    @Config
    @Label(name = "Food Stack Reduction", description = "Food stack sizes will be reduced based off their hunger restored and saturation multiplier. See 'Food Stack Reduction Formula' for the formula")
    public static Boolean foodStackReduction = true;
    @Config
    @Label(name = "Food Stack Reduction Formula", description = "The formula to calculate the stack size of a food item. Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html.")
    public static String foodStackReductionFormula = "ROUND((1 - (effectiveness - 1) / 24) * 64 * 0.25, 0)";
    @Config(min = 1, max = 64)
    @Label(name = "Stackable Stews", description = "Stews will stack up to this number. It's overridden by 'foodStackReduction' if enabled. Still affected by black/whitelist")
    public static Integer stackableSoups = 16;
    @Config(min = 0.01d, max = 64d)
    @Label(name = "Item Stack Multiplier", description = "Items max stack sizes (excluding blocks) will be multiplied by this value. Foods will be overridden by 'Food Stack Reduction' or 'Food Stack Multiplier' if are active. Setting to 1 will disable this feature.")
    public static Double itemStackMultiplier = 1d;
    @Config(min = 0.01d, max = 64d)
    @Label(name = "Block Stack Multiplier", description = "All the blocks max stack sizes will be multiplied by this value to increase / decrease them.")
    public static Double blockStackMultiplier = 1.0d;

	public StackSizes(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static void processStackSizes(boolean isClientSide) {
        synchronized (mutex) {
            resetStackSizes();
            processItemStackSizes(isClientSide);
            processBlockStackSizes(isClientSide);
            processStewStackSizes(isClientSide);
            processFoodStackSizes(isClientSide);
        }
    }

    private static final Object mutex = new Object();

    static HashMap<Item, Integer> originalStackSizes = new HashMap<>();
    public static void resetStackSizes() {
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
    public static void processItemStackSizes(boolean isClientSide) {
        if (itemStackMultiplier == 1d)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (item instanceof BlockItem
                    || item.maxStackSize == 1
                    || JsonFeature.isItemInTag(item, NO_STACK_SIZE_CHANGES, isClientSide))
                continue;

            double stackSize = entry.getValue() * itemStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Blocks
    public static void processBlockStackSizes(boolean isClientSide) {
        if (blockStackMultiplier == 1d)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (!(item instanceof BlockItem)
                    || item.maxStackSize == 1
                    || JsonFeature.isItemInTag(item, NO_STACK_SIZE_CHANGES, isClientSide))
                continue;

            double stackSize = entry.getValue() * blockStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }

    }

    //Stews
    public static void processStewStackSizes(boolean isClientSide) {
        if (stackableSoups == 1)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (!(item instanceof BowlFoodItem) && !(item instanceof SuspiciousStewItem)
                    || JsonFeature.isItemInTag(item, NO_STACK_SIZE_CHANGES, isClientSide))
                continue;

            item.maxStackSize = stackableSoups;
        }

    }

    //Food
    @SuppressWarnings("deprecation")
    public static void processFoodStackSizes(boolean isClientSide) {
        if (!foodStackReduction)
            return;

        for (Map.Entry<Item, Integer> entry : originalStackSizes.entrySet()) {
            Item item = entry.getKey();
            if (item.getFoodProperties() == null
                    || JsonFeature.isItemInTag(item, NO_STACK_SIZE_CHANGES, isClientSide))
                continue;

            FoodProperties food = item.getFoodProperties();
            int stackSize = (int) Utils.computeFoodFormula(food, foodStackReductionFormula);
            if (stackSize > 0)
                item.maxStackSize = Mth.clamp(stackSize, 1, 64);
        }
    }

    //Sync before json
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void syncFeatureConfig(OnDatapackSyncEvent event) {
        if (!this.isEnabled())
            return;
        processStackSizes(false);

        if (event.getPlayer() == null) {
            event.getPlayerList().getPlayers().forEach(player -> StackSizesSync.sync(foodStackReduction, foodStackReductionFormula, stackableSoups, itemStackMultiplier, blockStackMultiplier, player));
        }
        else {
            StackSizesSync.sync(foodStackReduction, foodStackReductionFormula, stackableSoups, itemStackMultiplier, blockStackMultiplier, event.getPlayer());
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
                if (!player.addItem(result))
                    player.drop(newResult, true);
            }
        }
    }
}