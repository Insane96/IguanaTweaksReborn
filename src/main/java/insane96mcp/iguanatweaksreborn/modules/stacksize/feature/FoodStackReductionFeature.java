package insane96mcp.iguanatweaksreborn.modules.stacksize.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.stacksize.classutils.CustomStackSize;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Food Stack Reduction", description = "Reduces food max stack based off the hunger they provide.")
public class FoodStackReductionFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Boolean> foodStackReductionConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodStackMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelistConfig;

    private static final List<String> blacklistDefault = Arrays.asList("minecraft:rotten_flesh", "minecraft:potion");

    public boolean foodStackReduction = true;
    public double foodStackMultiplier = 0.5d;
    public static List<IdTagMatcher> blacklist;
    public boolean blacklistAsWhitelist = false;


    public FoodStackReductionFeature(ITModule module) {
        super(module);

        Config.builder.comment(this.getDescription()).push(this.getName());
        foodStackReductionConfig = Config.builder
                .comment("Food stack sizes will be reduced based off their hunger restored and saturation multiplier. The formula is '(1/MAX(saturation, 1))*(3*(-hunger)+64-SQRT(hunger))'. E.g. Cooked Porkchops give 8 hunger points and have a 1.6 saturation multiplier so their stack size will be '(1/MAX(1.6, 1))*(3*(-8)+64-SQRT(8))' = 23 (Even foods that normally stack up to 16 will use the same formula, like Honey).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier' & 'Saturation Restore multiplier'\nNote that even soups will stack.")
                .define("Food Stack Reduction", foodStackReduction);
        foodStackMultiplierConfig = Config.builder
                .comment("All the foods max stack sizes will be multiplied by this value to increase / decrease them (after Food Stack Reduction). In the example with the Porkchop with this set to 0.5 Cooked Porkchops will stack up to 12.")
                .defineInRange("Food Stack Multiplier", foodStackMultiplier, 0.01d, 64d);
        blacklistConfig = Config.builder
                .comment("Items or tags that will ignore the food stack changes. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"#minecraft:fishes\", \"minecraft:cooked_porkchop\"].")
                .defineList("Items Blacklist", blacklistDefault, o -> o instanceof String);
        blacklistAsWhitelistConfig = Config.builder
                .comment("Items Blacklist will be treated as a whitelist.")
                .define("Blacklist as Whitelist", blacklistAsWhitelist);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        foodStackReduction = foodStackReductionConfig.get();
        foodStackMultiplier = foodStackMultiplierConfig.get();
        blacklist = parseBlacklist(blacklistConfig.get());
        blacklistAsWhitelist = blacklistAsWhitelistConfig.get();

        processFoodStackSizes();
    }

    private static List<IdTagMatcher> parseBlacklist(List<? extends String> list) {
        List<IdTagMatcher> idTagMatchers = new ArrayList<>();
        for (String line : list) {
            IdTagMatcher idTagMatcher = IdTagMatcher.parseLine(line);
            if (idTagMatcher != null)
                idTagMatchers.add(idTagMatcher);
        }
        return idTagMatchers;
    }

    public void processFoodStackSizes() {
        if (!this.isEnabled())
            return;

        if (!foodStackReduction)
            return;

        for (CustomStackSize defaultStackSize : Modules.stackSizeModule.defaultStackSizes) {
            Item item = ForgeRegistries.ITEMS.getValue(defaultStackSize.id);

            if (!item.isFood())
                continue;
            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : blacklist) {
                if (!blacklistAsWhitelist) {
                    if (MCUtils.isInTagOrItem(blacklistEntry, item, null)) {
                        isInBlacklist = true;
                        break;
                    }
                }
                else {
                    if (MCUtils.isInTagOrItem(blacklistEntry, item, null)) {
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
            double stackSize = (1d/Math.max(saturation, 1d))*(3d*(-hunger)+64d-Math.sqrt(hunger));
            stackSize *= foodStackMultiplier;
            stackSize = Math.max(stackSize, 1);
            stackSize = Math.min(stackSize, 64);
            item.maxStackSize = (int) Math.round(stackSize);
        }
    }

    @SubscribeEvent
    public void fixStackedSoupsEating(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack original = event.getItem();
            ItemStack result = event.getResultStack();
            if (original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET)) {
                ItemStack newResult = original.copy();
                newResult.setCount(original.getCount() - 1);
                event.setResultStack(newResult);
                player.addItemStackToInventory(result);
            }
        }
    }
}
