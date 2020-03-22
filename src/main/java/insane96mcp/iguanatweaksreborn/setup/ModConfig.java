package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.Config.CommonConfig.Farming.NerfedBonemeal;
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

        public static Boolean farming;
        public static Boolean experience;
        public static Boolean hardness;

        public static void load() {
            farming = Config.COMMON.modules.farming.get();
            experience = Config.COMMON.modules.experience.get();
            hardness = Config.COMMON.modules.hardness.get();
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
        public static NerfedBonemeal nerfedBonemeal;
        public static Boolean cropsRequireWater;
        public static Double cropsGrowthMultiplier;
        public static Double noSunlightGrowthMultiplier;
        public static Integer minSunlight;
        public static Double sugarCanesGrowthMultiplier;
        public static Double cactusGrowthMultiplier;

        public static void load() {
            nerfedBonemeal = Config.COMMON.farming.nerfedBonemeal.get();
            cropsRequireWater = Config.COMMON.farming.cropsRequireWater.get();
            cropsGrowthMultiplier = Config.COMMON.farming.cropsGrowthMultiplier.get();
            noSunlightGrowthMultiplier = Config.COMMON.farming.noSunlightGrowthMultiplier.get();
            minSunlight = Config.COMMON.farming.minSunlight.get();
            sugarCanesGrowthMultiplier = Config.COMMON.farming.sugarCanesGrowthMultiplier.get();
            cactusGrowthMultiplier = Config.COMMON.farming.cactusGrowthMultiplier.get();
        }

    }

    public static class Hardness {
        public static Double multiplier;
        public static List<DimensionMultiplier> dimensionMultipliers;
        public static List<CommonTagBlock> blacklist;
        public static Boolean blacklistAsWhitelist;
        public static List<BlockHardness> customHardness;

        public static void load() {
            multiplier = Config.COMMON.hardness.multiplier.get();
            dimensionMultipliers = ParseDimensionMultipliers(Config.COMMON.hardness.dimensionMultiplier.get());
            blacklist = ParseBlacklist(Config.COMMON.hardness.blacklist.get());
            blacklistAsWhitelist = Config.COMMON.hardness.backlistAsWhitelist.get();
            customHardness = ParseCustomHardnesses(Config.COMMON.hardness.customHardness.get());
        }

        public static List<DimensionMultiplier> ParseDimensionMultipliers(List<? extends String> list) {
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

        public static List<CommonTagBlock> ParseBlacklist(List<? extends String> list) {
            List<CommonTagBlock> commonTagBlock = new ArrayList<>();

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
                    CommonTagBlock hardness = new CommonTagBlock(null, tag, dimension);
                    commonTagBlock.add(hardness);
                }
                else {
                    if (!ResourceLocation.isResouceNameValid(split[0])) {
                        LogHelper.Warn("%s block for Hardness Blacklist is not valid", line);
                        continue;
                    }
                    ResourceLocation block = new ResourceLocation(split[0]);
                    if (ForgeRegistries.BLOCKS.containsKey(block)) {
                        CommonTagBlock hardness = new CommonTagBlock(block, null, dimension);
                        commonTagBlock.add(hardness);
                    }
                    else
                        LogHelper.Warn(String.format("%s block for Hardness Blacklist seems to not exist", line));
                }
            }
            return commonTagBlock;
        }

        public static class CommonTagBlock {
            public ResourceLocation block;
            public ResourceLocation tag;
            public ResourceLocation dimension;

            public CommonTagBlock(@Nullable ResourceLocation block, @Nullable ResourceLocation tag, ResourceLocation dimension) {
                if (block == null && tag == null) {
                    throw new NullPointerException("block and tag can't be both null");
                }
                this.block = block;
                this.tag = tag;
                this.dimension = dimension;
            }
        }

        public static List<BlockHardness> ParseCustomHardnesses(List<? extends String> list) {
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

        public static class BlockHardness extends CommonTagBlock {
            public double hardness;

            public BlockHardness(@Nullable ResourceLocation block, @Nullable ResourceLocation tag, Double hardness, ResourceLocation dimension) {
                super(block, tag, dimension);
                this.hardness = hardness;
            }
        }
    }

    public static class HUD {

    }

    private static void load() {
        Modules.load();
        Experience.load();
        Farming.load();
        Hardness.load();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
        ModConfig.load();
    }
}