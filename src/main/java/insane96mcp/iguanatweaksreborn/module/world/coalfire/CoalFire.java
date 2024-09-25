package insane96mcp.iguanatweaksreborn.module.world.coalfire;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import net.minecraft.world.level.GameRules;

@Label(name = "Coal & Fire")
@LoadFeature(module = Modules.Ids.WORLD)
public class CoalFire extends Feature {

    public static final GameRules.Key<GameRules.IntegerValue> RULE_FIRESPEEDMULTIPLIER = GameRules.register("iguanatweaks:fireSpeedMultiplier", GameRules.Category.UPDATES, GameRules.IntegerValue.create(4));

    /*@Config(min = 0d, max = 100)
    @Label(name = "Fire spread speed multiplier", description = "How much faster fire ticks and spreads.")
    public static Double fireSpreadSpeedMultiplier = 4d;*/

    @Config
    @Label(name = "Unlit campfire", description = "If true, campfires must be lit")
    public static Boolean unlitCampfires = false;

    @Config
    @Label(name = "Campfire turn off under rain", description = "If true, campfires will be extinguished when it starts to rain")
    public static Boolean campfireTurnOffUnderRain = true;

    public CoalFire(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean areCampfiresUnlit() {
        return Feature.isEnabled(CoalFire.class) && unlitCampfires;
    }

    public static boolean canRainTurnOffCampfires() {
        return Feature.isEnabled(CoalFire.class) && campfireTurnOffUnderRain;
    }
}