package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;

@Label(name = "Fluids")
@LoadFeature(module = Modules.Ids.WORLD)
public class Fluids extends Feature {
    @Config
    @Label(name = "Water fall damage", description = "If enabled, water will deal fall damage if too shallow")
    public static Boolean waterFallDamage = true;
    @Config
    @Label(name = "Water push force", description = "How strong does water push entities. Vanilla is 0.014")
    public static Double waterPushForce = 0.03d;
    @Config
    @Label(name = "Water pushes when no blocks are around", description = "If true water pushes entities down even when no blocks are around")
    public static Boolean waterPushesWhenNoBlocksAround = true;

    public Fluids(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean shouldOverrideWaterFallDamageModifier() {
        return Feature.isEnabled(Fluids.class) && waterFallDamage;
    }

    public static boolean shouldWaterPushWhenNoBlocksAround() {
        return Feature.isEnabled(Fluids.class) && waterPushesWhenNoBlocksAround;
    }
}