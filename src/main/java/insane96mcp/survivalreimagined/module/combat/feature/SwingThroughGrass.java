package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Swing Through Grass", description = "Players are able to hit mobs through no collision blocks like grass or torches.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class SwingThroughGrass extends Feature {
	public SwingThroughGrass(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}