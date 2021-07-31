package insane96mcp.iguanatweaksreborn.modules.stacksize.feature;

import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.Modules;
import insane96mcp.iguanatweaksreborn.modules.misc.feature.WeightFeature;
import insane96mcp.iguanatweaksreborn.modules.stacksize.classutils.CustomStackSize;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

@Label(name = "Stack Reduction", description = "Make food, items and blocks less stackable")
public class StackReductionFeature extends Feature {

    //Food
    private final ForgeConfigSpec.ConfigValue<Boolean> foodStackReductionConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodStackMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> stackableSoupsConfig;
    //Items
    private final ForgeConfigSpec.ConfigValue<Double> itemStackMultiplierConfig;
    //Blocks
    private final ForgeConfigSpec.ConfigValue<Boolean> blockStackReductionConfig;
    private final ForgeConfigSpec.ConfigValue<Double> blockStackMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> blockStackAffectedByMaterialConfig;
    //Blacklist
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelistConfig;

    private static final List<String> blacklistDefault = Arrays.asList("minecraft:rotten_flesh", "minecraft:potion");

    public boolean foodStackReduction = true;
    public double foodStackMultiplier = 0.5d;
    public boolean stackableSoups = false;
    public double itemStackMultiplier = 0.5d;
    public boolean blockStackReduction = true;
    public double blockStackMultiplier = 1.0d;
    public boolean blockStackAffectedByMaterial = true;
    public List<IdTagMatcher> blacklist;
	public boolean blacklistAsWhitelist = false;

	public StackReductionFeature(Module module) {
        super(Config.builder, module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        foodStackReductionConfig = Config.builder
                .comment("Food stack sizes will be reduced based off their hunger restored and saturation multiplier. The formula is '(1/MAX(saturation, 1))*(3*(-hunger)+64-SQRT(hunger))'. E.g. Cooked Porkchops give 8 hunger points and have a 1.6 saturation multiplier so their stack size will be '(1/MAX(1.6, 1))*(3*(-8)+64-SQRT(8))' = 23 (Even foods that don't usually stack up to 16 or that don't stack at all will use the same formula, like Honey or Stews).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier' & 'Saturation Restore multiplier'")
                .define("Food Stack Reduction", foodStackReduction);
        foodStackMultiplierConfig = Config.builder
                .comment("All the foods max stack sizes will be multiplied by this value to increase / decrease them (after Food Stack Reduction). In the example with the Porkchop with this set to 0.5 Cooked Porkchops will stack up to 12.")
                .defineInRange("Food Stack Multiplier", foodStackMultiplier, 0.01d, 64d);
        stackableSoupsConfig = Config.builder
                .comment("If true, soups will stack like normal food.")
                .define("Stackable Soups", this.stackableSoups);
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
        blacklistConfig = Config.builder
                .comment("Items or tags that will ignore the stack changes. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"#minecraft:fishes\", \"minecraft:stone\"].")
                .defineList("Items Blacklist", blacklistDefault, o -> o instanceof String);
        blacklistAsWhitelistConfig = Config.builder
                .comment("Items Blacklist will be treated as a whitelist.")
                .define("Blacklist as Whitelist", blacklistAsWhitelist);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.foodStackReduction = this.foodStackReductionConfig.get();
        this.foodStackMultiplier = this.foodStackMultiplierConfig.get();
        this.stackableSoups = this.stackableSoupsConfig.get();
        this.blacklist = IdTagMatcher.parseStringList(this.blacklistConfig.get());
        this.blacklistAsWhitelist = this.blacklistAsWhitelistConfig.get();
        this.itemStackMultiplier = this.itemStackMultiplierConfig.get();
        this.blockStackReduction = this.blockStackReductionConfig.get();
        this.blockStackMultiplier = this.blockStackMultiplierConfig.get();
        this.blockStackAffectedByMaterial = this.blockStackAffectedByMaterialConfig.get();
        processItemStackSizes();
        processBlockStackSizes();
        processFoodStackSizes();
    }

    //Items
    public void processItemStackSizes() {
        if (!this.isEnabled())
            return;

        if (itemStackMultiplier == 1d)
            return;
        for (CustomStackSize defaultStackSize : Modules.stackSize.defaultStackSizes) {
            Item item = ForgeRegistries.ITEMS.getValue(defaultStackSize.id);
            if (item instanceof BlockItem)
                continue;
            if (item.maxStackSize == 1)
                continue;
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : blacklist) {
                if (!blacklistAsWhitelist) {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInBlacklist = true;
                        break;
                    }
                }
                else {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInWhitelist = true;
                        break;
                    }
                }
            }
            if (isInBlacklist || (!isInWhitelist && blacklistAsWhitelist))
                continue;
            double stackSize = defaultStackSize.stackSize * itemStackMultiplier;
            stackSize = MathHelper.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Blocks
    public void processBlockStackSizes() {
        if (!this.isEnabled())
            return;
        if (!blockStackReduction)
            return;
        for (CustomStackSize defaultStackSize : Modules.stackSize.defaultStackSizes) {
            Item item = ForgeRegistries.ITEMS.getValue(defaultStackSize.id);
            if (!(item instanceof BlockItem))
                continue;
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : blacklist) {
                if (!blacklistAsWhitelist) {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInBlacklist = true;
                        break;
                    }
                }
                else {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInWhitelist = true;
                        break;
                    }
                }
            }
            if (isInBlacklist || (!isInWhitelist && blacklistAsWhitelist))
                continue;
            Block block = ((BlockItem) item).getBlock();
            double weight = WeightFeature.getStateWeight(block.getDefaultState());
            if (!this.blockStackAffectedByMaterial)
                weight = 1d;
            double stackSize = (defaultStackSize.stackSize / weight) * blockStackMultiplier;
            stackSize = MathHelper.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    //Food
    public void processFoodStackSizes() {
        if (!this.isEnabled())
            return;

        if (!foodStackReduction)
            return;
        for (CustomStackSize defaultStackSize : Modules.stackSize.defaultStackSizes) {
            Item item = ForgeRegistries.ITEMS.getValue(defaultStackSize.id);
            if (!item.isFood())
                continue;
            if ((item instanceof SoupItem || item instanceof SuspiciousStewItem) && !this.stackableSoups)
                continue;
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : blacklist) {
                if (!blacklistAsWhitelist) {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInBlacklist = true;
                        break;
                    }
                }
                else {
                    if (blacklistEntry.matchesItem(item, null)) {
                        isInWhitelist = true;
                        break;
                    }
                }
            }
            if (isInBlacklist)
                continue;
            if (!isInWhitelist && blacklistAsWhitelist)
                continue;
            int hunger = item.getFood().value;
            double saturation = item.getFood().saturation;
            double stackSize = (1d / Math.max(saturation * 2d, 1d)) * (3d * (-hunger) + 64d - Math.sqrt(hunger));
            stackSize *= foodStackMultiplier;
            stackSize = MathHelper.clamp(stackSize, 1, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    @SubscribeEvent
    public void fixStackedSoupsEating(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack original = event.getItem();
            ItemStack result = event.getResultStack();
            if (original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET || result.getItem() == Items.GLASS_BOTTLE)) {
                ItemStack newResult = original.copy();
                newResult.setCount(original.getCount() - 1);
                event.setResultStack(newResult);
                player.addItemStackToInventory(result);
            }
        }
    }
}
