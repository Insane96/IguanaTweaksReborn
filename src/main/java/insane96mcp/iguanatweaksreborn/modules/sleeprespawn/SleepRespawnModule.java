package insane96mcp.iguanatweaksreborn.modules.sleeprespawn;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.IModule;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature.SleepingFeature;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class SleepRespawnModule {
    static SleepingFeature sleepingFeature;
    public static void init() {
        sleepingFeature = new SleepingFeature();
    }
}
