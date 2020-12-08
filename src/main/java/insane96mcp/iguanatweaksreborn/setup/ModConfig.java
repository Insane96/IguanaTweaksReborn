package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.HungerHealthModule;
import insane96mcp.iguanatweaksreborn.modules.StackSizesModule;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    public static class Modules {

        public static boolean farming;
        public static boolean experience;
        public static boolean hardness;
        public static boolean stackSizes;
        public static boolean hungerHealth;
        public static boolean sleepRespawn;
        public static boolean misc;

        public static void load() {
            farming = Config.COMMON.modules.farming.get();
            experience = Config.COMMON.modules.experience.get();
            hardness = Config.COMMON.modules.hardness.get();
            stackSizes = Config.COMMON.modules.stackSizes.get();
            hungerHealth = Config.COMMON.modules.hungerHealth.get();
            sleepRespawn = Config.COMMON.modules.sleepRespawn.get();
            misc = Config.COMMON.modules.misc.get();
        }
    }

    public static class Experience {
        public static Double oreMultiplier;
        public static Double globalMultiplier;
        public static Double mobsFromSpawnersMultiplier;

        public static void load() {
            oreMultiplier = Config.COMMON.experience.oreMultiplier.get();
            globalMultiplier = Config.COMMON.experience.globalMultiplier.get();
            mobsFromSpawnersMultiplier = Config.COMMON.experience.mobsFromSpawnersMultiplier.get();
        }
    }

    public static class Farming {

        public static class Livestock {
            public static double childGrowthMultiplier;
            public static double breedingMultiplier;
            public static double eggLayMultiplier;
            public static int cowMilkDelay;

            public static void load() {
                childGrowthMultiplier = Config.COMMON.farming.livestock.childGrowthMultiplier.get();
                breedingMultiplier = Config.COMMON.farming.livestock.breedingMultiplier.get();
                eggLayMultiplier = Config.COMMON.farming.livestock.eggLayMultiplier.get();
                cowMilkDelay = Config.COMMON.farming.livestock.cowMilkDelay.get();
            }
        }

        public static class Agriculture {
            public static FarmingModule.Agriculture.NerfedBonemeal nerfedBonemeal;
            public static Double bonemealFailChance;
            public static FarmingModule.Agriculture.CropsRequireWater cropsRequireWater;
            public static Double cropsGrowthMultiplier;
            public static Double noSunlightGrowthMultiplier;
            public static Integer minSunlight;
            public static Double sugarCanesGrowthMultiplier;
            public static Double cactusGrowthMultiplier;
            public static Double cocoaBeansGrowthMultiplier;
            public static Double netherwartGrowthMultiplier;
            public static Double chorusPlantGrowthMultiplier;
            public static Double saplingGrowthMultiplier;
            public static Double stemGrowthMultiplier;
            public static Double berryBushGrowthMultiplier;
            public static Double kelpGrowthMultiplier;
            public static Double bambooGrowthMultiplier;

            public static List<HoeCooldown> hoesCooldowns;
            public static boolean disableLowTierHoes;
            public static int hoesDamageOnUseMultiplier;

            public static void load() {
                nerfedBonemeal = Config.COMMON.farming.agriculture.nerfedBonemeal.get();
                bonemealFailChance = Config.COMMON.farming.agriculture.bonemealFailChance.get();
                cropsRequireWater = Config.COMMON.farming.agriculture.cropsRequireWater.get();
                cropsGrowthMultiplier = Config.COMMON.farming.agriculture.cropsGrowthMultiplier.get();
                noSunlightGrowthMultiplier = Config.COMMON.farming.agriculture.noSunlightGrowthMultiplier.get();
                minSunlight = Config.COMMON.farming.agriculture.minSunlight.get();
                sugarCanesGrowthMultiplier = Config.COMMON.farming.agriculture.sugarCanesGrowthMultiplier.get();
                cactusGrowthMultiplier = Config.COMMON.farming.agriculture.cactusGrowthMultiplier.get();
                cocoaBeansGrowthMultiplier = Config.COMMON.farming.agriculture.cocoaBeansGrowthMultiplier.get();
                netherwartGrowthMultiplier = Config.COMMON.farming.agriculture.netherwartGrowthMultiplier.get();
                chorusPlantGrowthMultiplier = Config.COMMON.farming.agriculture.chorusPlantGrowthMultiplier.get();
                saplingGrowthMultiplier = Config.COMMON.farming.agriculture.saplingGrowthMultiplier.get();
                stemGrowthMultiplier = Config.COMMON.farming.agriculture.stemGrowthMultiplier.get();
                berryBushGrowthMultiplier = Config.COMMON.farming.agriculture.berryBushGrowthMultiplier.get();
                kelpGrowthMultiplier = Config.COMMON.farming.agriculture.kelpGrowthMultiplier.get();
                bambooGrowthMultiplier = Config.COMMON.farming.agriculture.bambooGrowthMultiplier.get();
                hoesCooldowns = parseHoesCooldowns(Config.COMMON.farming.agriculture.hoesCooldowns.get());
                disableLowTierHoes = Config.COMMON.farming.agriculture.disableLowTierHoes.get();
                hoesDamageOnUseMultiplier = Config.COMMON.farming.agriculture.hoesDamageOnUseMultiplier.get();
            }

            public static List<HoeCooldown> parseHoesCooldowns(List<? extends String> list) {
                List<HoeCooldown> hoesCooldowns = new ArrayList<>();
                for (String line : list) {
                    String[] split = line.split(",");
                    if (split.length != 2) {
                        LogHelper.Warn("Invalid line \"%s\" for Hoe Cooldown", line);
                        continue;
                    }
                    if (!NumberUtils.isParsable(split[1])) {
                        LogHelper.Warn(String.format("Invalid chance \"%s\" for Hoe Cooldown", line));
                        continue;
                    }
                    int cooldown = Integer.parseInt(split[1]);
                    if (split[0].startsWith("#")) {
                        String replaced = split[0].replace("#", "");
                        ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                        if (tag == null) {
                            LogHelper.Warn("%s tag for Hoe Cooldown is not valid", replaced);
                            continue;
                        }
                        HoeCooldown hoeCooldown = new HoeCooldown(null, tag, cooldown);
                        hoesCooldowns.add(hoeCooldown);
                    }
                    else {
                        ResourceLocation block = ResourceLocation.tryCreate(split[0]);
                        if (block == null) {
                            LogHelper.Warn("%s item for Hoe Cooldown is not valid", split[0]);
                            continue;
                        }
                        if (ForgeRegistries.ITEMS.containsKey(block)) {
                            HoeCooldown hoeCooldown = new HoeCooldown(block, null, cooldown);
                            hoesCooldowns.add(hoeCooldown);
                        }
                        else
                            LogHelper.Warn(String.format("%s item for Hoe Till Chance seems to not exist", split[0]));
                    }
                }
                return hoesCooldowns;
            }

            public static class HoeCooldown extends IdTagMatcher {
                public int cooldown;

                public HoeCooldown(@Nullable ResourceLocation item, @Nullable ResourceLocation tag, int cooldown) {
                    super(item, tag);
                    this.cooldown = cooldown;
                }
            }
        }

        public static void load() {
            Agriculture.load();
            Livestock.load();
        }

    }

    public static class Hardness {
        public static Double multiplier;
        public static List<DimensionMultiplier> dimensionMultipliers;
        public static List<IdTagMatcher> blacklist;
        public static Boolean blacklistAsWhitelist;
        public static List<BlockHardness> customHardness;

        public static void load() {
            multiplier = Config.COMMON.hardness.multiplier.get();
            dimensionMultipliers = parseDimensionMultipliers(Config.COMMON.hardness.dimensionMultiplier.get());
            blacklist = parseBlacklist(Config.COMMON.hardness.blacklist.get());
            blacklistAsWhitelist = Config.COMMON.hardness.backlistAsWhitelist.get();
            customHardness = parseCustomHardnesses(Config.COMMON.hardness.customHardness.get());
        }

        public static List<DimensionMultiplier> parseDimensionMultipliers(List<? extends String> list) {
            List<DimensionMultiplier> dimensionMultipliers = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length < 1 || split.length > 2) {
                    LogHelper.Warn("Invalid line \"%s\" for Dimension multiplier. Format must be modid:dimensionId,hardness", line);
                    continue;
                }
                ResourceLocation dimension = ResourceLocation.tryCreate(split[0]);
                if (dimension == null) {
                    LogHelper.Warn(String.format("Invalid dimension \"%s\" for Dimension multiplier", split[0]));
                    continue;
                }
                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid hardness \"%s\" for Dimension Multiplier", split[1]));
                    continue;
                }
                double hardness = Double.parseDouble(split[1]);

                dimensionMultipliers.add(new DimensionMultiplier(dimension, hardness));
            }

            return dimensionMultipliers;
        }

        public static class DimensionMultiplier {
            public ResourceLocation dimension;
            public double multiplier;

            public DimensionMultiplier(ResourceLocation dimension, double multiplier) {
                this.dimension = dimension;
                this.multiplier = multiplier;
            }
        }

        public static List<IdTagMatcher> parseBlacklist(List<? extends String> list) {
            List<IdTagMatcher> commonTagBlock = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length < 1 || split.length > 2) {
                    LogHelper.Warn("Invalid line \"%s\" for Hardnesses Blacklist. Format must be modid:blockid,modid:dimension", line);
                    continue;
                }
                ResourceLocation dimension = Utils.AnyRL;
                if (split.length == 2) {
                    dimension = ResourceLocation.tryCreate(split[1]);
                    if (dimension == null) {
                        LogHelper.Warn(String.format("Invalid dimension \"%s\" for Hardness Blacklist. Ignoring it", split[1]));
                        dimension = Utils.AnyRL;
                    }
                }
                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                    if (tag == null) {
                        LogHelper.Warn("%s tag for Hardness Blacklist is not valid", replaced);
                        continue;
                    }
                    IdTagMatcher hardness = new IdTagMatcher(null, tag, dimension);
                    commonTagBlock.add(hardness);
                }
                else {
                    ResourceLocation block = ResourceLocation.tryCreate(split[0]);
                    if (block == null) {
                        LogHelper.Warn("%s block for Hardness Blacklist is not valid", line);
                        continue;
                    }
                    if (ForgeRegistries.BLOCKS.containsKey(block)) {
                        IdTagMatcher hardness = new IdTagMatcher(block, null, dimension);
                        commonTagBlock.add(hardness);
                    }
                    else
                        LogHelper.Warn(String.format("%s block for Hardness Blacklist seems to not exist", line));
                }
            }
            return commonTagBlock;
        }

        public static List<BlockHardness> parseCustomHardnesses(List<? extends String> list) {
            ArrayList<BlockHardness> blockHardnesses = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length < 2 || split.length > 3) {
                    LogHelper.Warn("Invalid line \"%s\" for Custom Hardnesses", line);
                    continue;
                }
                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid hardness \"%s\" for Custom Hardnesses", line));
                    continue;
                }
                double hardness = Double.parseDouble(split[1]);
                ResourceLocation dimension = Utils.AnyRL;
                if (split.length == 3) {
                    dimension = ResourceLocation.tryCreate(split[2]);
                    if (dimension == null) {
                        LogHelper.Warn(String.format("Invalid dimension \"%s\" for Custom Hardnesses. Ignoring it", split[2]));
                        dimension = Utils.AnyRL;
                    }
                }
                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                    if (tag == null) {
                        LogHelper.Warn("%s tag for Custom Hardneses is not valid", replaced);
                        continue;
                    }
                    BlockHardness blockHardness = new BlockHardness(null, tag, hardness, dimension);
                    blockHardnesses.add(blockHardness);
                }
                else {
                    ResourceLocation block = ResourceLocation.tryCreate(split[0]);
                    if (block == null) {
                        LogHelper.Warn("%s block for Custom Hardneses is not valid", split[0]);
                        continue;
                    }
                    if (ForgeRegistries.BLOCKS.containsKey(block)) {
                        BlockHardness blockHardness = new BlockHardness(block, null, hardness, dimension);
                        blockHardnesses.add(blockHardness);
                    }
                    else
                        LogHelper.Warn(String.format("%s block for Custom Hardnesses seems to not exist", split[0]));
                }
            }

            return blockHardnesses;
        }

        public static class BlockHardness extends IdTagMatcher {
            public double hardness;

            public BlockHardness(@Nullable ResourceLocation block, @Nullable ResourceLocation tag, Double hardness, ResourceLocation dimension) {
                super(block, tag, dimension);
                this.hardness = hardness;
            }
        }
    }

    public static class StackSizes {

        public static boolean foodStackReduction;
        public static double foodStackMultiplier;
        public static List<CustomStackSize> customStackList;
        public static List<IdTagMatcher> blacklist;
        public static boolean blacklistAsWhitelist;
        public static double itemStackMultiplier;
        public static boolean blockStackReduction;
        public static double blockStackMultiplier;

        public static void load() {
            foodStackReduction = Config.COMMON.stackSizes.foodStackReduction.get();
            foodStackMultiplier = Config.COMMON.stackSizes.foodStackMultiplier.get();
            customStackList = parseCustomStackList(Config.COMMON.stackSizes.customStackList.get());
            blacklist = parseBlacklist(Config.COMMON.stackSizes.blacklist.get());
            blacklistAsWhitelist = Config.COMMON.stackSizes.blacklistAsWhitelist.get();
            itemStackMultiplier = Config.COMMON.stackSizes.itemStackMultiplier.get();
            blockStackReduction = Config.COMMON.stackSizes.blockStackReduction.get();
            blockStackMultiplier = Config.COMMON.stackSizes.blockStackMultiplier.get();
            StackSizesModule.processItemStackSizes();
            StackSizesModule.processFoodStackSizes();
            StackSizesModule.processBlockStackSizes();
            StackSizesModule.processCustomStackSizes();
        }

        public static class CustomStackSize extends IdTagMatcher {
            public int stackSize;

            public CustomStackSize(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, int stackSize) {
                super(id, tag);
                this.stackSize = stackSize;
            }
        }

        private static List<CustomStackSize> parseCustomStackList(List<? extends String> list) {
            ArrayList<CustomStackSize> stackSizes = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length != 2) {
                    LogHelper.Warn("Invalid line \"%s\" for Custom Stack Size", line);
                    continue;
                }
                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid stackSize \"%s\" for Custom Stack Size", line));
                    continue;
                }
                int stackSize = Integer.parseInt(split[1]);
                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                    if (tag == null) {
                        LogHelper.Warn("%s tag for Custom Stack Size is not valid", replaced);
                        continue;
                    }
                    CustomStackSize customStackSize = new CustomStackSize(null, tag, stackSize);
                    stackSizes.add(customStackSize);
                }
                else {
                    ResourceLocation item = ResourceLocation.tryCreate(split[0]);
                    if (item == null) {
                        LogHelper.Warn("%s item for Custom Stack Size is not valid", split[0]);
                        continue;
                    }
                    if (ForgeRegistries.ITEMS.containsKey(item)) {
                        CustomStackSize customStackSize = new CustomStackSize(item, null, stackSize);
                        stackSizes.add(customStackSize);
                    }
                    else
                        LogHelper.Warn(String.format("%s item for Custom Stack Size seems to not exist", split[0]));
                }
            }
            return stackSizes;
        }

        private static List<IdTagMatcher> parseBlacklist(List<? extends String> list) {
            List<IdTagMatcher> idTagMatchers = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length != 1) {
                    LogHelper.Warn("Invalid line \"%s\" for Item Stack Sizes Blacklist. Format must be modid:blockid", line);
                    continue;
                }
                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                    if (tag == null) {
                        LogHelper.Warn("%s tag for Item Stack Sizes Blacklist is not valid", replaced);
                        continue;
                    }
                    IdTagMatcher itemTag = new IdTagMatcher(null, tag);
                    idTagMatchers.add(itemTag);
                }
                else {
                    ResourceLocation item = ResourceLocation.tryCreate(split[0]);
                    if (item == null) {
                        LogHelper.Warn("%s item for Item Stack Sizes Blacklist is not valid", line);
                        continue;
                    }
                    if (ForgeRegistries.ITEMS.containsKey(item)) {
                        IdTagMatcher itemId = new IdTagMatcher(item, null);
                        idTagMatchers.add(itemId);
                    }
                    else
                        LogHelper.Warn(String.format("%s item for Item Stack Sizes Blacklist seems to not exist", line));
                }
            }
            return idTagMatchers;
        }
    }

    public static class HungerHealth {

        public static double foodHungerMultiplier;
        public static double foodSaturationMultiplier;
        public static List<CustomFoodValue> customFoodValue;
        public static List<IdTagMatcher> blacklist;
        public static boolean blacklistAsWhitelist;
        public static double foodHealMultiplier;
        public static double blockBreakExaustionMultiplier;
        public static List<Debuff> debuffs;

        public static void load() {
            foodHungerMultiplier = Config.COMMON.hungerHealth.foodHungerMultiplier.get();
            foodSaturationMultiplier = Config.COMMON.hungerHealth.foodSaturationMultiplier.get();
            customFoodValue = parseCustomFoodHungerList(Config.COMMON.hungerHealth.customFoodValue.get());
            blacklist = parseBlacklist(Config.COMMON.hungerHealth.blacklist.get());
            blacklistAsWhitelist = Config.COMMON.hungerHealth.blacklistAsWhitelist.get();
            foodHealMultiplier = Config.COMMON.hungerHealth.foodHealMultiplier.get();
            blockBreakExaustionMultiplier = Config.COMMON.hungerHealth.blockBreakExaustionMultiplier.get();
            debuffs = parseDebuffs(Config.COMMON.hungerHealth.debuffs.get());

            HungerHealthModule.processFoodMultipliers();
            HungerHealthModule.processCustomFoodValues();
        }

        private static List<IdTagMatcher> parseBlacklist(List<? extends String> list) {
            List<IdTagMatcher> idTagMatchers = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length != 1) {
                    LogHelper.Warn("Invalid line \"%s\" for Food Restore Blacklist. Format must be modid:item_id", line);
                    continue;
                }
                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    ResourceLocation tag = ResourceLocation.tryCreate(replaced);
                    if (tag == null) {
                        LogHelper.Warn("%s tag for Food Restore Blacklist is not valid", replaced);
                        continue;
                    }
                    IdTagMatcher itemTag = new IdTagMatcher(null, tag);
                    idTagMatchers.add(itemTag);
                }
                else {
                    ResourceLocation item = ResourceLocation.tryCreate(split[0]);
                    if (item == null) {
                        LogHelper.Warn("%s item for Food Restore Blacklist is not valid", line);
                        continue;
                    }
                    if (ForgeRegistries.ITEMS.containsKey(item)) {
                        IdTagMatcher itemId = new IdTagMatcher(item, null);
                        idTagMatchers.add(itemId);
                    }
                    else
                        LogHelper.Warn(String.format("%s item for Food Restore Blacklist seems to not exist", line));
                }
            }
            return idTagMatchers;
        }

        public static class CustomFoodValue {
            public ResourceLocation id;
            public int hunger;
            public float saturation;

            public CustomFoodValue(ResourceLocation id, int hunger, float saturation) {
                this.id = id;
                this.hunger = hunger;
                this.saturation = saturation;
            }
        }

        private static List<CustomFoodValue> parseCustomFoodHungerList(List<? extends String> list) {
            ArrayList<CustomFoodValue> foodValues = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length < 2 || split.length > 3) {
                    LogHelper.Warn("Invalid line \"%s\" for Custom Food Value", line);
                    continue;
                }
                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid hunger \"%s\" for Custom Food Value", line));
                    continue;
                }
                int hunger = Integer.parseInt(split[1]);
                float saturation = -1f;
                if (split.length == 3) {
                    if (!NumberUtils.isParsable(split[2])) {
                        LogHelper.Warn(String.format("Invalid saturation \"%s\" for Custom Food Value", line));
                        continue;
                    }
                    saturation = Float.parseFloat(split[1]);
                }
                ResourceLocation item = ResourceLocation.tryCreate(split[0]);
                if (item == null) {
                    LogHelper.Warn("%s item for Custom Food Value is not valid", split[0]);
                    continue;
                }
                if (ForgeRegistries.ITEMS.containsKey(item) && ForgeRegistries.ITEMS.getValue(item).isFood()) {
                    CustomFoodValue customFoodValue = new CustomFoodValue(item, hunger, saturation);
                    foodValues.add(customFoodValue);
                }
                else
                    LogHelper.Warn(String.format("%s item for Custom Food Value seems to not exist or is not a food", split[0]));
            }
            return foodValues;
        }

        public static class Debuff {
            public Stat stat;
            public double min, max;
            public Effect effect;
            public int amplifier;

            public Debuff(Stat stat, double min, double max, Effect effect, int amplifier) {
                this.stat = stat;
                this.min = min;
                this.max = max;
                this.effect = effect;
                this.amplifier = amplifier;
            }

            @Override
            public String toString() {
                return String.format("Debuff{stat: %s, min: %f, max: %f, effect: %s, amplifier: %d}", stat, min, max, effect.getRegistryName(), amplifier);
            }

            public enum Stat {
                HUNGER,
                HEALTH,
                EXPERIENCE_LEVEL
            }
        }

        private static List<Debuff> parseDebuffs(List<? extends String> list) {
            ArrayList<Debuff> debuffs = new ArrayList<>();
            for (String line : list) {
                //Split
                String[] split = line.split(",");
                if (split.length != 4) {
                    LogHelper.Warn("Invalid line \"%s\" for Debuffs", line);
                    continue;
                }
                //Stat
                Debuff.Stat stat = Utils.searchEnum(Debuff.Stat.class, split[0]);
                if (stat == null) {
                    LogHelper.Warn(String.format("Invalid stat name \"%s\" for Debuff", line));
                    continue;
                }

                //Range
                double min = -Double.MAX_VALUE, max = Double.MAX_VALUE;
                if (split[1].contains("..")) {
                    String[] rangeSplit = split[1].split("\\.\\.");
                    if (rangeSplit.length < 1 || rangeSplit.length > 2) {
                        LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                        continue;
                    }
                    if (rangeSplit[0].length() > 0) {
                        if (!NumberUtils.isParsable(rangeSplit[0])) {
                            LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                            continue;
                        }
                        min = Double.parseDouble(rangeSplit[0]);
                    }
                    if (rangeSplit.length == 2 && rangeSplit[1].length() > 0) {
                        if (!NumberUtils.isParsable(rangeSplit[1])) {
                            LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                            continue;
                        }
                        max = Double.parseDouble(rangeSplit[1]);
                    }
                }
                else {
                    if (!NumberUtils.isParsable(split[1])) {
                        LogHelper.Warn(String.format("Invalid range \"%s\" for Debuff", line));
                        continue;
                    }
                    double value = Double.parseDouble(split[1]);
                    min = value;
                    max = value;
                }

                //Potion effect
                ResourceLocation effectRL = ResourceLocation.tryCreate(split[2]);
                if (effectRL == null) {
                    LogHelper.Warn("%s potion effect for Debuff is not valid", split[2]);
                    continue;
                }
                if (!ForgeRegistries.POTIONS.containsKey(effectRL)) {
                    LogHelper.Warn("%s potion effect for Debuff seems to not exist", split[2]);
                    continue;
                }
                Effect effect = ForgeRegistries.POTIONS.getValue(effectRL);

                //Amplifier
                if (!NumberUtils.isParsable(split[3])) {
                    LogHelper.Warn(String.format("Invalid amplifier \"%s\" for Debuff", line));
                    continue;
                }
                int amplifier = Integer.parseInt(split[3]);

                Debuff debuff = new Debuff(stat, min, max, effect, amplifier);
                //LogHelper.Info(debuff.toString());
                debuffs.add(debuff);
            }

            return debuffs;
        }
    }

    public static class SleepRespawn {

        public static int hungerDepletedOnWakeUp;
        public static List<EffectOnWakeUp> effectsOnWakeUp;
        public static boolean noSleepIfHungry;

        public static void load() {
            hungerDepletedOnWakeUp = Config.COMMON.sleepRespawn.hungerDepletedOnWakeUp.get();
            effectsOnWakeUp = parseEffectsOnWakeUp(Config.COMMON.sleepRespawn.effectsOnWakeUp.get());
            noSleepIfHungry = Config.COMMON.sleepRespawn.noSleepIfHungry.get();
        }

        public static class EffectOnWakeUp {
            public ResourceLocation potionId;
            public int duration;
            public int amplifier;

            public EffectOnWakeUp(ResourceLocation potionId, int duration, int amplifier) {
                this.potionId = potionId;
                this.duration = duration;
                this.amplifier = amplifier;
            }
        }

        private static List<EffectOnWakeUp> parseEffectsOnWakeUp(List<? extends String> list) {
            List<EffectOnWakeUp> effectsOnWakeUp = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(",");
                if (split.length != 3) {
                    LogHelper.Warn("Invalid line \"%s\" for Effects on WakeUp. Format must be modid:potion_id,duration_in_ticks,amplifier", line);
                    continue;
                }
                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid duration \"%s\" for Effects on WakeUp", split[1]));
                    continue;
                }
                int duration = Integer.parseInt(split[1]);
                if (!NumberUtils.isParsable(split[2])) {
                    LogHelper.Warn(String.format("Invalid amplifier \"%s\" for Effects on WakeUp", split[1]));
                    continue;
                }
                int amplifier = Integer.parseInt(split[2]);
                ResourceLocation potion = ResourceLocation.tryCreate(split[0]);
                if (potion == null) {
                    LogHelper.Warn("%s potion for Effects on WakeUp is not valid", line);
                    continue;
                }
                if (ForgeRegistries.POTIONS.containsKey(potion)) {
                    EffectOnWakeUp effectOnWakeUp = new EffectOnWakeUp(potion, duration, amplifier);
                    effectsOnWakeUp.add(effectOnWakeUp);
                }
                else
                    LogHelper.Warn(String.format("%s potion for Effects on WakeUp seems to not exist", line));
            }
            return effectsOnWakeUp;
        }
    }

    public static class Misc {
        public static void load() {

        }
    }

    private static void load() {
        Modules.load();
        Experience.load();
        Farming.load();
        Hardness.load();
        HungerHealth.load();
        StackSizes.load();
        SleepRespawn.load();
        Misc.load();
    }

    public static class IdTagMatcher {
        public ResourceLocation id;
        public ResourceLocation tag;
        public ResourceLocation dimension;

        public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag, ResourceLocation dimension) {
            if (id == null && tag == null) {
                throw new NullPointerException("block and tag can't be both null");
            }
            this.id = id;
            this.tag = tag;
            this.dimension = dimension;
        }

        public IdTagMatcher(@Nullable ResourceLocation id, @Nullable ResourceLocation tag) {
            this(id, tag, Utils.AnyRL);
        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
        ModConfig.load();
    }
}