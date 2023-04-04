package insane96mcp.survivalreimagined.module.client.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.ClientModules;

@Label(name = "Misc", description = "Misc client side changes")
@LoadFeature(module = ClientModules.Ids.CLIENT)
public class Misc extends Feature {

    public Misc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }
}
