package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
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
        public static boolean misc;

        public static void load() {

            farming = Config.COMMON.modules.farming.get();
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


    public static void load() {
        Modules.load();
        Farming.load();
    }
}