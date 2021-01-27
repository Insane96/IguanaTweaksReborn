package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.StackSizesModule;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
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
        public static boolean stackSizes;
        public static boolean hungerHealth;
        public static boolean misc;

        public static void load() {

            farming = Config.COMMON.modules.farming.get();
            stackSizes = Config.COMMON.modules.stackSizes.get();
            hungerHealth = Config.COMMON.modules.hungerHealth.get();
            misc = Config.COMMON.modules.misc.get();
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


        public static double blockBreakExaustionMultiplier;
        public static List<Debuff> debuffs;

        public static void load() {

            blockBreakExaustionMultiplier = Config.COMMON.hungerHealth.blockBreakExaustionMultiplier.get();
            debuffs = parseDebuffs(Config.COMMON.hungerHealth.debuffs.get());
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
                debuffs.add(debuff);
            }

            return debuffs;
        }
    }

    public static void load() {
        Modules.load();
        Farming.load();
        HungerHealth.load();
        StackSizes.load();
    }
}