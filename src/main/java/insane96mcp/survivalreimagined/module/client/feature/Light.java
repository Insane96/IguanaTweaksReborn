package insane96mcp.survivalreimagined.module.client.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.ClientModules;

@Label(name = "Light", description = "Changes to light")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Light extends Feature {

    public static final int NIGHT_VISION_FADE_OUT_AT = 40;

    @Config
    @Label(name = "No Night Vision Flashing", description = "If true night vision will no longer flash 10 seconds before expiring, instead will slowly fade out 2 seconds before expiring.")
    public static Boolean noNightVisionFlashing = true;

    public Light(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean shouldDisableNightVisionFlashing() {
        return isEnabled(Light.class) && noNightVisionFlashing;
    }
}
