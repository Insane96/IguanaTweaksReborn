package insane96mcp.iguanatweaksreborn.module.client.feature;

import insane96mcp.iguanatweaksreborn.module.ClientModules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;

@Label(name = "Misc", description = "Misc client side changes")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Misc extends Feature {

    @Config
    @Label(name = "Remove potion enchant glint", description = "Pretty self explanatory")
    public static Boolean removePotionEnchantGlint = true;

    public Misc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean shouldRemovePotionEnchantGlint() {
        return isEnabled(Misc.class) && removePotionEnchantGlint;
    }
}
