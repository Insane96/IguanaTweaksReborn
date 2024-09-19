package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;

@Label(name = "Swing Through Grass", description = "Players are able to hit mobs through no collision blocks like grass or torches.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class SwingThroughGrass extends Feature {
	public SwingThroughGrass(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}