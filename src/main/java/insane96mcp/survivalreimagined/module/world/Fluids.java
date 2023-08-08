package insane96mcp.survivalreimagined.module.world;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Fluids")
@LoadFeature(module = Modules.Ids.WORLD)
public class Fluids extends Feature {
    @Config
    @Label(name = "Water fall damage", description = "If enabled, water will deal fall damage if too shallow")
    public static Boolean waterFallDamage = true;

    public Fluids(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

   public static boolean shouldOverrideWaterFallDamageModifier() {
        return Feature.isEnabled(Fluids.class) && waterFallDamage;
   }
}