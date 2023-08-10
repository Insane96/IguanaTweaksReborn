package insane96mcp.survivalreimagined.module.world.leaves.integration;

import snownee.kiwi.config.KiwiConfigManager;
import snownee.passablefoliage.PassableFoliageCommonConfig;

public class PassableFoliage {
    public static void changeConfigOptions() {
        PassableFoliageCommonConfig.fallDamageReduction = 0.2f;
        PassableFoliageCommonConfig.fallDamageThreshold = 6;
        KiwiConfigManager.getHandler(PassableFoliageCommonConfig.class).save();
    }
}
