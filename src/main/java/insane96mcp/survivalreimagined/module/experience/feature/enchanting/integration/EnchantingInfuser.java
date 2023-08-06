package insane96mcp.survivalreimagined.module.experience.feature.enchanting.integration;

import fuzs.enchantinginfuser.config.ServerConfig;

public class EnchantingInfuser {
    public static void setConfigOptions() {
        fuzs.enchantinginfuser.EnchantingInfuser.CONFIG.get(ServerConfig.class).normalInfuser.maximumBookshelves = 25;
        fuzs.enchantinginfuser.EnchantingInfuser.CONFIG.get(ServerConfig.class).normalInfuser.types.allowAnvilEnchantments = true;
    }
}
