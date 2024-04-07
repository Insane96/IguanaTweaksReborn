package insane96mcp.iguanatweaksreborn.module.client;

import insane96mcp.iguanatweaksreborn.module.ClientModules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;

@Label(name = "Misc", description = "Misc client side changes")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Misc extends Feature {

    @Config(min = 0d, max = 1d)
    @Label(name = "World Border Transparency", description = "Multiplies the world border transparency by this value")
    public static Double worldBorderTransparency = 0.4d;

    @Config
    @Label(name = "Shorter world border", description = "If true, the world border height is reduced by 4 times.")
    public static Boolean shorterWorldBorder = true;

    @Config(min = 1d)
    @Label(name = "Cap world border height", description = "Set the max height of the world border.")
    public static Double capWorldBorderHeight = 128d;

    @Config
    @Label(name = "No tilting with some damage types", description = "If true, camera will not tilt when taking magic, wither, on fire, cramming, drowning and thorns damage.")
    public static Boolean noTiltingWithSomeDamageTypes = true;

    public Misc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static float getWorldBorderTransparencyMultiplier() {
        if (isEnabled(Misc.class))
            return worldBorderTransparency.floatValue();
        return 1f;
    }

    public static boolean shouldShortenWorldBorder() {
        return isEnabled(Misc.class) && shorterWorldBorder;
    }

    public static boolean shouldDisableTiltingWithSomeDamageTypes() {
        return isEnabled(Misc.class) && noTiltingWithSomeDamageTypes;
    }
}
