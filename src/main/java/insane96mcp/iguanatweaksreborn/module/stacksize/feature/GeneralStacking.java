package insane96mcp.iguanatweaksreborn.module.stacksize.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Label(name = "General Stacking", description = "Make food, items and blocks less stackable. Items and Blocks are disabled by default.")
@LoadFeature(module = Modules.Ids.STACK_SIZE)
public class GeneralStacking extends Feature {
    //TODO doesn't work as tags are loaded later in a world and not when the game starts
    public static final ResourceLocation NO_STACK_SIZE_CHANGES = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_stack_size_changes");

    @Config
    @Label(name = "Food Stack Reduction", description = "Food stack sizes will be reduced based off their hunger restored and saturation multiplier. The formula is '(1 - (effective_quality - 1) / Food Quality Divider) * 64' where effective_quality is hunger+saturation restored. E.g. Cooked Porkchops give 8 hunger points and have a 0.8 saturation multiplier so their stack size will be '(1 - (20.8 - 1) / 18.5) * 64' = 24 (Even foods that usually stack up to 16 or that don't stack at all will use the same formula, like Honey or Stews).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier' & 'Saturation Restore multiplier'")
    public static Boolean foodStackReduction = true;
    @Config(min = 0d, max = 40d)
    @Label(name = "Food Quality Divider", description = "Used in the 'Food Stack Reduction' formula. Increase this if there are foods that are better than vanilla ones, otherwise they will all stack to 1. Set this to 21.8 if you disable 'Hunger Restore Multiplier'")
    public static Double foodQualityDivider = 18.5d;
    @Config(min = 0.01d, max = 64d)
    @Label(name = "Food Stack Multiplier", description = "All the foods max stack sizes will be multiplied by this value to increase / decrease them (after Food Stack Reduction).")
    public static Double foodStackMultiplier = 0.9d;
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
    public void readConfig(final ModConfigEvent event) {
        super.readConfig(event);
        processItemStackSizes();
        processBlockStackSizes();
        processStewStackSizes();
        processFoodStackSizes();
    }

    private final Object mutex = new Object();

    HashMap<Item, Integer> originalItemStackSizes = new HashMap<>();
    //Items
    public void processItemStackSizes() {
        if (!this.isEnabled())
            return;
        if (itemStackMultiplier == 1d)
            return;

        synchronized (mutex) {
            if (originalItemStackSizes.isEmpty()) {
                for (Item item : ForgeRegistries.ITEMS.getValues()) {
                    if (item instanceof BlockItem)
                        continue;
                    originalItemStackSizes.put(item, item.maxStackSize);
                }
            }

            for (Map.Entry<Item, Integer> entry : originalItemStackSizes.entrySet()) {
                Item item = entry.getKey();
                if (item.maxStackSize == 1)
                    continue;
                if (Utils.isItemInTag(item, NO_STACK_SIZE_CHANGES))
                    continue;

                double stackSize = entry.getValue() * itemStackMultiplier;
                stackSize = Mth.clamp(stackSize, 1, 64);
                item.maxStackSize = (int) Math.round(stackSize);
            }
        }
    }

    HashMap<Item, Integer> originalBlockStackSizes = new HashMap<>();
    //Blocks
    public void processBlockStackSizes() {
        if (!this.isEnabled())
            return;
        if (!blockStackReduction)
            return;
        synchronized (mutex) {
            if (originalBlockStackSizes.isEmpty()) {
                for (Item item : ForgeRegistries.ITEMS.getValues()) {
                    if (!(item instanceof BlockItem))
                        continue;
                    originalBlockStackSizes.put(item, item.maxStackSize);
                }
            }

            for (Map.Entry<Item, Integer> entry : originalBlockStackSizes.entrySet()) {
                Item item = entry.getKey();
                if (item.maxStackSize == 1)
                    continue;
                if (Utils.isItemInTag(item, NO_STACK_SIZE_CHANGES))
                    continue;

                Block block = ((BlockItem) item).getBlock();
                double weight = blockStackAffectedByMaterial ? Weights.getWeightForState(block.defaultBlockState()) : 1d;
                double stackSize = (entry.getValue() / weight) * blockStackMultiplier;
                stackSize = Mth.clamp(stackSize, 1, 64);
                item.maxStackSize = (int) Math.round(stackSize);
            }
        }
    }

    //Stews
    public void processStewStackSizes() {
        if (!this.isEnabled())
            return;
        if (stackableSoups == 1)
            return;
        synchronized (mutex) {
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                if (!(item instanceof BowlFoodItem) && !(item instanceof SuspiciousStewItem))
                    continue;
                if (Utils.isItemInTag(item, NO_STACK_SIZE_CHANGES))
                    continue;

                item.maxStackSize = Math.round(stackableSoups);
            }
        }
    }

    //Food
    public void processFoodStackSizes() {
        if (!this.isEnabled())
            return;
        if (!foodStackReduction)
            return;
        synchronized (mutex) {
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                if (!item.isEdible())
                    continue;
                if (Utils.isItemInTag(item, NO_STACK_SIZE_CHANGES))
                    continue;

                int hunger = item.getFoodProperties().getNutrition();
                double saturation = item.getFoodProperties().getSaturationModifier();
                double effectiveQuality = hunger + (hunger * saturation * 2d);
                double stackSize = (1 - (effectiveQuality - 1) / foodQualityDivider) * 64;
                stackSize *= foodStackMultiplier;
                stackSize = Mth.clamp(stackSize, 1, 64);
                item.maxStackSize = (int) Math.round(stackSize);
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