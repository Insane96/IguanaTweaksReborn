package insane96mcp.iguanatweaksreborn.module.stacksize.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.Weights;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

@Label(name = "General Stacking", description = "Make food, items and blocks less stackable. Items and Blocks are disabled by default. Changes in this section require a Minecraft restart")
public class GeneralStacking extends Feature {

    private final Object mutex = new Object();

    //Food
    private final ForgeConfigSpec.ConfigValue<Boolean> foodStackReductionConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodQualityDividerConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodStackMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Integer> stackableSoupsConfig;
    //Items
    private final ForgeConfigSpec.ConfigValue<Double> itemStackMultiplierConfig;
    //Blocks
    private final ForgeConfigSpec.ConfigValue<Boolean> blockStackReductionConfig;
    private final ForgeConfigSpec.ConfigValue<Double> blockStackMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> blockStackAffectedByMaterialConfig;
    //Blacklist
    private final BlacklistConfig blacklistConfig;

    private static final List<String> blacklistDefault = Arrays.asList("minecraft:rotten_flesh");

    public boolean foodStackReduction = true;
    public double foodQualityDivider = 18.5;
    public double foodStackMultiplier = 0.6d;
    public int stackableSoups = 16;
    public double itemStackMultiplier = 1d;
    public boolean blockStackReduction = false;
    public double blockStackMultiplier = 1.0d;
    public boolean blockStackAffectedByMaterial = true;
    public List<IdTagMatcher> blacklist;
	public boolean blacklistAsWhitelist = false;

	public GeneralStacking(Module module) {
        super(Config.builder, module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        foodStackReductionConfig = Config.builder
                .comment("Food stack sizes will be reduced based off their hunger restored and saturation multiplier. The formula is '(1 - (effective_quality - 1) / Food Quality Divider) * 64' where effective_quality is hunger+saturation restored. E.g. Cooked Porkchops give 8 hunger points and have a 0.8 saturation multiplier so their stack size will be '(1 - (20.8 - 1) / 18.5) * 64' = 24 (Even foods that usually stack up to 16 or that don't stack at all will use the same formula, like Honey or Stews).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier' & 'Saturation Restore multiplier'")
                .define("Food Stack Reduction", foodStackReduction);
        foodQualityDividerConfig = Config.builder
                .comment("Used in the 'Food Stack Reduction' formula. Increase this if there are foods that are better than vanilla ones, otherwise they will all stack to 1. Set this to 21.8 if you disable 'Hunger Restore Multiplier'")
                .defineInRange("Food Quality Divider", this.foodQualityDivider, 1d, 40d);
        foodStackMultiplierConfig = Config.builder
                .comment("All the foods max stack sizes will be multiplied by this value to increase / decrease them (after Food Stack Reduction).")
                .defineInRange("Food Stack Multiplier", foodStackMultiplier, 0.01d, 64d);
        stackableSoupsConfig = Config.builder
                .comment("Stews will stack up to this number. It's overridden by 'foodStackReduction' if enabled. Still affected by black/whitelist")
                .defineInRange("Stackable Stews", this.stackableSoups, 1, 64);
        itemStackMultiplierConfig = Config.builder
                .comment("Items max stack sizes (excluding blocks) will be multiplied by this value. Foods will be overridden by 'Food Stack Reduction' or 'Food Stack Multiplier' if are active. Setting to 1 will disable this feature.")
                .defineInRange("Item Stack Multiplier", itemStackMultiplier, 0.01d, 1.0d);
        blockStackReductionConfig = Config.builder
                .comment("Blocks max stack sizes will be reduced based off their material.")
                .define("Block Stack Reduction", blockStackReduction);
        blockStackMultiplierConfig = Config.builder
                .comment("All the blocks max stack sizes will be multiplied by this value to increase / decrease them. This is applied after the reduction from 'Block Stack Reduction'.")
                .defineInRange("Block Stack Multiplier", blockStackMultiplier, 0.01d, 64d);
        blockStackAffectedByMaterialConfig = Config.builder
                .comment("When true, block stacks are affected by both their material type and the block stack multiplier. If false, block stacks will be affected by the multiplier only.")
                .define("Block Stack Affected by Material", blockStackAffectedByMaterial);
        blacklistConfig = new BlacklistConfig(Config.builder, "Blacklist", "Items or tags that will ignore the stack changes. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"#minecraft:fishes\", \"minecraft:stone\"].", blacklistDefault, this.blacklistAsWhitelist);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.foodStackReduction = this.foodStackReductionConfig.get();
        this.foodQualityDivider = this.foodQualityDividerConfig.get();
        this.foodStackMultiplier = this.foodStackMultiplierConfig.get();
        this.stackableSoups = this.stackableSoupsConfig.get();
        this.blacklist = (List<IdTagMatcher>) IdTagMatcher.parseStringList(this.blacklistConfig.listConfig.get());
        this.blacklistAsWhitelist = this.blacklistConfig.listAsWhitelistConfig.get();
        this.itemStackMultiplier = this.itemStackMultiplierConfig.get();
        this.blockStackReduction = this.blockStackReductionConfig.get();
        this.blockStackMultiplier = this.blockStackMultiplierConfig.get();
        this.blockStackAffectedByMaterial = this.blockStackAffectedByMaterialConfig.get();
        processItemStackSizes();
        processBlockStackSizes();
        processStewStackSizes();
        processFoodStackSizes();
        /*for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!(item instanceof BlockItem))
                continue;

            Block block = ((BlockItem) item).getBlock();
            Arrays.stream(Material.class.getFields())
                    .filter(m -> {
                        try {
                            return m.get(null).equals(block.defaultBlockState().getMaterial());
                        } catch (IllegalAccessException e) {
                            LogHelper.error("%s", e.toString());
                        }
                        return false;
                    })
                    .findFirst().ifPresentOrElse(m -> LogHelper.info("%s %s", item.getRegistryName(), m.getName()), () -> LogHelper.info("%s %s", item.getRegistryName(), "Nope"));
        }*/
    }

    private boolean processedItems = false;
    private boolean processedBlocks = false;
    private boolean processedFood = false;
    private boolean processedStews = false;

    //Items
    public void processItemStackSizes() {
        if (!this.isEnabled())
            return;
        synchronized (mutex) {
            if (processedItems)
                return;
            processedItems = true;
        }
        if (itemStackMultiplier == 1d)
            return;

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof BlockItem)
                continue;
            if (item.maxStackSize == 1)
                continue;

            //Check for item black/whitelist
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : this.blacklist) {
                if (blacklistEntry.matchesItem(item)) {
                    if (!this.blacklistAsWhitelist)
                        isInBlacklist = true;
                    else
                        isInWhitelist = true;
                    break;
                }
            }
            if (isInBlacklist || (!isInWhitelist && this.blacklistAsWhitelist))
                continue;

            double stackSize = item.maxStackSize * itemStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Blocks
    public void processBlockStackSizes() {
        if (!this.isEnabled())
            return;
        synchronized (mutex) {
            if (processedBlocks)
                return;
            processedBlocks = true;
        }
        if (!blockStackReduction)
            return;

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!(item instanceof BlockItem))
                continue;

            //Check for blocks black/whitelist
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : this.blacklist) {
                if (blacklistEntry.matchesItem(item)) {
                    if (!this.blacklistAsWhitelist)
                        isInBlacklist = true;
                    else
                        isInWhitelist = true;
                    break;
                }
            }
            if (isInBlacklist || (!isInWhitelist && this.blacklistAsWhitelist))
                continue;
            Block block = ((BlockItem) item).getBlock();
            double weight = Weights.getStateWeight(block.defaultBlockState());
            if (!this.blockStackAffectedByMaterial)
                weight = 1d;
            double stackSize = (item.maxStackSize / weight) * blockStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Stews
    public void processStewStackSizes() {
        if (!this.isEnabled())
            return;
        synchronized (mutex) {
            if (processedStews)
                return;
            processedStews = true;
        }
        if (this.stackableSoups == 1)
            return;

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!item.isEdible() && !(item instanceof BowlFoodItem || item instanceof SuspiciousStewItem))
                continue;
            //Check for food black/whitelist
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : this.blacklist) {
                if (blacklistEntry.matchesItem(item)) {
                    if (!this.blacklistAsWhitelist)
                        isInBlacklist = true;
                    else
                        isInWhitelist = true;
                    break;
                }
            }
            if (isInBlacklist || (!isInWhitelist && this.blacklistAsWhitelist))
                continue;
            int stackSize = this.stackableSoups;
            item.maxStackSize = Math.round(stackSize);
        }
    }

    //Food
    public void processFoodStackSizes() {
        if (!this.isEnabled())
            return;
        synchronized (mutex) {
            if (processedFood)
                return;
            processedFood = true;
        }
        if (!foodStackReduction)
            return;

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!item.isEdible())
                continue;
            //Check for food black/whitelist
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : this.blacklist) {
                if (blacklistEntry.matchesItem(item)) {
                    if (!this.blacklistAsWhitelist)
                        isInBlacklist = true;
                    else
                        isInWhitelist = true;
                    break;
                }
            }
            if (isInBlacklist || (!isInWhitelist && this.blacklistAsWhitelist))
                continue;
            int hunger = item.getFoodProperties().getNutrition();
            double saturation = item.getFoodProperties().getSaturationModifier();
            double effectiveQuality = hunger + (hunger * saturation * 2d);
            double stackSize = (1 - (effectiveQuality - 1) / this.foodQualityDivider) * 64;
            stackSize *= foodStackMultiplier;
            stackSize = Mth.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
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