package insane96mcp.iguanatweaksreborn.module.world.weather;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.message.FoggySync;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
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
    public static Integer thunderstormIntensityBaseDuration = 4;

    @Config
    @Label(name = "Foggy Weather.Enabled")
    public static Boolean foggyWeather = true;
    @Config(min = 1)
    @Label(name = "Foggy Weather.Min Time", description = "Minimum time (in minutes) a foggy weather can last.")
    public static Integer foggyWeatherMinTime = 1;
    @Config(min = 1)
    @Label(name = "Foggy Weather.Max Time", description = "Maximum time (in minutes) a foggy weather can last.")
    public static Integer foggyWeatherMaxTime = 20;

    public Weather(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)
                || serverLevel.dimension() != Level.OVERWORLD)
            return;
        serverLevel.getDataStorage().computeIfAbsent(WeatherSavedData::load, WeatherSavedData::new, IguanaTweaksReborn.RESOURCE_PREFIX + "weather");
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (!this.isEnabled()
                || event.phase != TickEvent.Phase.START
                || event.level.dimension() != Level.OVERWORLD
                || event.level.isClientSide
                /*|| event.level.tickCount % 20 != 10*/)
            return;

        tickFoggyWeather(event.level);
        tickVariableThunderstorm(event.level);
    }

    public static WeatherSavedData weatherSavedData = new WeatherSavedData();

    public static void tickFoggyWeather(Level level) {
        if (!foggyWeather)
            return;

        if (weatherSavedData.foggyData.targetTime == -1) {
            weatherSavedData.foggyData.targetTime = getNewFoggyTargetTime(level.random);
        }
        if (++weatherSavedData.foggyData.timer >= weatherSavedData.foggyData.targetTime) {
            if (weatherSavedData.foggyData.current == weatherSavedData.foggyData.target) {
                weatherSavedData.foggyData.target = Foggy.values()[level.random.nextInt(Foggy.values().length)];
                weatherSavedData.foggyData.timer = 0;
                weatherSavedData.foggyData.targetTime = getNewFoggyTargetTime(level.random) / 2;
                level.players().forEach(player ->
                        FoggySync.sync((ServerPlayer) player, weatherSavedData.foggyData.timer, weatherSavedData.foggyData.targetTime, weatherSavedData.foggyData.current, weatherSavedData.foggyData.target));
            }
            else {
                weatherSavedData.foggyData.current = weatherSavedData.foggyData.target;
                weatherSavedData.foggyData.timer = 0;
                weatherSavedData.foggyData.targetTime = getNewFoggyTargetTime(level.random);
            }
        }
        weatherSavedData.setDirty();
    }

    private static int getNewFoggyTargetTime(RandomSource random) {
        return (int) ((random.nextFloat() * (foggyWeatherMaxTime - foggyWeatherMinTime) + foggyWeatherMinTime) * 60 * 20);
    }

    public static void tickVariableThunderstorm(Level level) {
        if (!thunderstormIntensity)
            return;

        if (weatherSavedData.thunderIntensityData.targetIntensity == -1)
            weatherSavedData.thunderIntensityData.targetIntensity = level.random.nextInt(14) + 1;

        if (--weatherSavedData.thunderIntensityData.timer <= 0) {
            weatherSavedData.thunderIntensityData.timer = (int) ((4 * 60 * 20) + level.random.nextFloat() * (4 * 60 * 20)) / weatherSavedData.thunderIntensityData.intensity;
            int delta = weatherSavedData.thunderIntensityData.targetIntensity > weatherSavedData.thunderIntensityData.intensity ? 1 : -1;
            weatherSavedData.thunderIntensityData.intensity += delta;
            if (weatherSavedData.thunderIntensityData.intensity == weatherSavedData.thunderIntensityData.targetIntensity)
                weatherSavedData.thunderIntensityData.targetIntensity = level.random.nextInt(14) + 1;
        }
        weatherSavedData.setDirty();
    }

    public static int getLightningStrikeChance(int original) {
        if (!Feature.isEnabled(Weather.class)
                || !thunderstormIntensity)
            return original;
        return original / weatherSavedData.thunderIntensityData.intensity;
    }

}
