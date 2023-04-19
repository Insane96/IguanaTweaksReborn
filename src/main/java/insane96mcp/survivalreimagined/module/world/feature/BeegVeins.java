package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Beeg Veins", description = "Add Beeg Veins to the world")
@LoadFeature(module = Modules.Ids.WORLD)
public class BeegVeins extends Feature {

    public BeegVeins(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }
}
