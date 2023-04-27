package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Fast Leaf Decay", description = "Makes leaves decay faster")
@LoadFeature(module = Modules.Ids.WORLD, enabledByDefault = false)
public class FastLeafDecay extends Feature {

	public FastLeafDecay(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}


}