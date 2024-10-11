package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Weather")
@LoadFeature(module = Modules.Ids.WORLD)
public class Weather extends Feature {
    public static final String THUNDERSTORM_INTENSITY = IguanaTweaksReborn.RESOURCE_PREFIX + "thunderstorm_intensity";
    public static final String THUNDERSTORM_TARGET_INTENSITY = IguanaTweaksReborn.RESOURCE_PREFIX + "thunderstorm_target_intensity";
    public static final String THUNDERSTORM_INTENSITY_TIMER = IguanaTweaksReborn.RESOURCE_PREFIX + "thunderstorm_intensity_timer";

    @Config
    @Label(name = "Thunderstorm Intensity.Enabled", description = "If true, thunderstorms can be range from different intensities, increasing / decreasing the lightning bolt chance")
    public static Boolean thunderstormIntensity = true;
    @Config(min = 1)
    @Label(name = "Thunderstorm Intensity.Min Intensity", description = "Minimum thunderstorm intensity.")
    public static Integer thunderstormIntensityMin = 1;
    @Config(min = 1)
    @Label(name = "Thunderstorm Intensity.Max Intensity", description = "Maximum thunderstorm intensity.")
    public static Integer thunderstormIntensityMax = 15;
    @Config(min = 1)
    @Label(name = "Thunderstorm Intensity.Base Duration", description = "Base duration of each intensity (in minutes). Lasts less and less the higher the intensity")
    public static Integer baseDuration = 4;

    public Weather(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    private static int thunderstormTimer = 0;
    private static int targetIntensity = -1;
    private static int intensity = 5;

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!this.isEnabled()
                || event.level.isClientSide
                || event.phase != TickEvent.Phase.START
                || event.level.dimension() != Level.OVERWORLD
                /*|| event.level.tickCount % 20 != 10*/)
            return;

        tickVariableThunderstorm(event.level);
    }

    public static void tickFog(Level level) {
        if (!thunderstormIntensity)
            return;

        if (targetIntensity == -1)
            targetIntensity = level.random.nextInt(14) + 1;

        //for (Player player : event.level.players())
        //player.displayClientMessage(Component.literal("Thunderstorm intensity: %d, target intensity: %d, timer: %d".formatted(intensity, targetIntensity, thunderstormTimer)), true);

        if (--thunderstormTimer > 0)
            return;

        thunderstormTimer = (int) ((baseDuration * 60 * 20) + level.random.nextFloat() * (baseDuration * 60 * 20)) / intensity;
        int delta = targetIntensity > intensity ? 1 : -1;
        intensity += delta;
        if (intensity == targetIntensity)
            targetIntensity = level.random.nextInt(thunderstormIntensityMin, thunderstormIntensityMax + 1);
    }

    public static void tickVariableThunderstorm(Level level) {
        if (!thunderstormIntensity)
            return;

        if (targetIntensity == -1)
            targetIntensity = level.random.nextInt(14) + 1;

        //for (Player player : event.level.players())
        //player.displayClientMessage(Component.literal("Thunderstorm intensity: %d, target intensity: %d, timer: %d".formatted(intensity, targetIntensity, thunderstormTimer)), true);

        if (--thunderstormTimer > 0)
            return;

        thunderstormTimer = (int) ((4 * 60 * 20) + level.random.nextFloat() * (4 * 60 * 20)) / intensity;
        int delta = targetIntensity > intensity ? 1 : -1;
        intensity += delta;
        if (intensity == targetIntensity)
            targetIntensity = level.random.nextInt(14) + 1;
    }

    public static int getLightningStrikeChance(int original) {
        if (!Feature.isEnabled(Weather.class)
                || !thunderstormIntensity)
            return original;
        return original / intensity;
    }
}
