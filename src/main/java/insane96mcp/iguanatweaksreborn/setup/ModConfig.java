package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.StackSizesModule;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
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

        public static void load() {
            farming = Config.COMMON.modules.farming.get();
            experience = Config.COMMON.modules.experience.get();
            hardness = Config.COMMON.modules.hardness.get();
            stackSizes = Config.COMMON.modules.stackSizes.get();
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
                        if (!ResourceLocation.isResouceNameValid(replaced)) {
                            LogHelper.Warn("%s tag for Hoe Cooldown is not valid", replaced);
                            continue;
                        }
                        ResourceLocation tag = new ResourceLocation(replaced);
                        HoeCooldown hoeCooldown = new HoeCooldown(null, tag, cooldown);
                        hoesCooldowns.add(hoeCooldown);
                    }
                    else {
                        if (!ResourceLocation.isResouceNameValid(split[0])) {
                            LogHelper.Warn("%s item for Hoe Cooldown is not valid", split[0]);
                            continue;
                        }
                        ResourceLocation block = new ResourceLocation(split[0]);
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

                ResourceLocation dimension = Utils.AnyRL;
                if (ResourceLocation.isResouceNameValid(split[0]))
                    dimension = new ResourceLocation(split[0]);
                else {
                    LogHelper.Warn(String.format("Invalid dimension \"%s\" for Dimension multiplier", split[0]));
                    continue;
                }

                if (!NumberUtils.isParsable(split[1])) {
                    LogHelper.Warn(String.format("Invalid hardness \"%s\" for Dimension Multiplier", split[1]));
                    continue;
                }
                Double hardness = Double.parseDouble(split[1]);

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
                if (split.length == 2)
                    if (ResourceLocation.isResouceNameValid(split[1]))
                        dimension = new ResourceLocation(split[1]);
                    else
                        LogHelper.Warn(String.format("Invalid dimension \"%s\" for Hardness Blacklist. Ignoring it", split[2]));

                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    if (!ResourceLocation.isResouceNameValid(replaced)) {
                        LogHelper.Warn("%s tag for Hardness Blacklist is not valid", replaced);
                        continue;
                    }
                    ResourceLocation tag = new ResourceLocation(replaced);
                    IdTagMatcher hardness = new IdTagMatcher(null, tag, dimension);
                    commonTagBlock.add(hardness);
                }
                else {
                    if (!ResourceLocation.isResouceNameValid(split[0])) {
                        LogHelper.Warn("%s block for Hardness Blacklist is not valid", line);
                        continue;
                    }
                    ResourceLocation block = new ResourceLocation(split[0]);
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
                Double hardness = Double.parseDouble(split[1]);
                ResourceLocation dimension = new ResourceLocation("any");
                if (split.length == 3)
                    if (ResourceLocation.isResouceNameValid(split[2]))
                        dimension = new ResourceLocation(split[2]);
                    else
                        LogHelper.Warn(String.format("Invalid dimension \"%s\" for Custom Hardnesses. Ignoring it", split[2]));

                if (split[0].startsWith("#")) {
                    String replaced = split[0].replace("#", "");
                    if (!ResourceLocation.isResouceNameValid(replaced)) {
                        LogHelper.Warn("%s tag for Custom Hardneses is not valid", replaced);
                        continue;
                    }
                    ResourceLocation tag = new ResourceLocation(replaced);
                    BlockHardness blockHardness = new BlockHardness(null, tag, hardness, dimension);
                    blockHardnesses.add(blockHardness);
                }
                else {
                    if (!ResourceLocation.isResouceNameValid(split[0])) {
                        LogHelper.Warn("%s block for Custom Hardneses is not valid", split[0]);
                        continue;
                    }
                    ResourceLocation block = new ResourceLocation(split[0]);
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

        public static void load() {
            foodStackReduction = Config.COMMON.stackSizes.foodStackReduction.get();
            foodStackMultiplier = Config.COMMON.stackSizes.foodStackMultiplier.get();
            customStackList = parseCustomStackList(Config.COMMON.stackSizes.customStackList.get());
            blacklist = parseBlacklist(Config.COMMON.stackSizes.blacklist.get());
            blacklistAsWhitelist = Config.COMMON.stackSizes.blacklistAsWhitelist.get();
            StackSizesModule.processFoodStackSizes();
            //StackSizesModule.processCustomStackSizes();
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
                    if (!ResourceLocation.isResouceNameValid(replaced)) {
                        LogHelper.Warn("%s tag for Custom Stack Size is not valid", replaced);
                        continue;
                    }
                    ResourceLocation tag = new ResourceLocation(replaced);
                    CustomStackSize customStackSize = new CustomStackSize(null, tag, stackSize);
                    stackSizes.add(customStackSize);
                }
                else {
                    if (!ResourceLocation.isResouceNameValid(split[0])) {
                        LogHelper.Warn("%s item for Custom Stack Size is not valid", split[0]);
                        continue;
                    }
                    ResourceLocation item = new ResourceLocation(split[0]);
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
                    if (!ResourceLocation.isResouceNameValid(replaced)) {
                        LogHelper.Warn("%s tag for Item Stack Sizes Blacklist is not valid", replaced);
                        continue;
                    }
                    ResourceLocation tag = new ResourceLocation(replaced);
                    IdTagMatcher itemTag = new IdTagMatcher(null, tag);
                    idTagMatchers.add(itemTag);
                }
                else {
                    if (!ResourceLocation.isResouceNameValid(split[0])) {
                        LogHelper.Warn("%s item for Item Stack Sizes Blacklist is not valid", line);
                        continue;
                    }
                    ResourceLocation item = new ResourceLocation(split[0]);
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

    private static void load() {
        Modules.load();
        Experience.load();
        Farming.load();
        Hardness.load();
        StackSizes.load();
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

    public static boolean loadedFoodChanges = false;

    @SubscribeEvent
    public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
        ModConfig.load();
    }
}