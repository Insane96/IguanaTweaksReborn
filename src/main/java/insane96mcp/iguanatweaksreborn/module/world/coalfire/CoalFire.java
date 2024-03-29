package insane96mcp.iguanatweaksreborn.module.world.coalfire;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;

@Label(name = "Coal & Fire")
@LoadFeature(module = Modules.Ids.WORLD)
public class CoalFire extends Feature {

    @Config(min = 0d, max = 100)
    @Label(name = "Fire spread speed multiplier", description = "How much faster fire ticks and spreads.")
    public static Double fireSpreadSpeedMultiplier = 4d;

    @Config
    @Label(name = "Unlit campfire", description = "If true, campfires must be lit")
    public static Boolean unlitCampfires = false;

    public CoalFire(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean changeFireSpreadSpeed() {
        return Feature.isEnabled(CoalFire.class) && fireSpreadSpeedMultiplier != 1d;
    }

    public static boolean areCampfiresUnlit() {
        return Feature.isEnabled(CoalFire.class) && unlitCampfires;
    }
}