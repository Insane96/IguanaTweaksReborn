package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
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
    @Label(name = "Variable Thunderstorm intensity", description = "If true, thunderstorms can be range from different intensities, increasing / decreasing the lightning bolt chance")
    public static Boolean variableThunderstormIntensity = true;

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
                || !variableThunderstormIntensity
                || event.level.dimension() != Level.OVERWORLD
                /*|| event.level.tickCount % 20 != 10*/)
            return;

        if (targetIntensity == -1)
            targetIntensity = event.level.random.nextInt(14) + 1;

        for (Player player : event.level.players())
            player.displayClientMessage(Component.literal("Thunderstorm intensity: %d, target intensity: %d, timer: %d".formatted(intensity, targetIntensity, thunderstormTimer)), true);

        if (--thunderstormTimer > 0)
            return;

        thunderstormTimer = (int) ((4 * 60 * 20) + event.level.random.nextFloat() * (4 * 60 * 20)) / intensity;
        int delta = targetIntensity > intensity ? 1 : -1;
        intensity += delta;
        if (intensity == targetIntensity)
            targetIntensity = event.level.random.nextInt(14) + 1;
    }

    public static int getLightningStrikeChance(int original) {
        if (!Feature.isEnabled(Weather.class)
                || !variableThunderstormIntensity)
            return original;
        return original / intensity;
    }
}
