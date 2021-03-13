package insane96mcp.iguanatweaksreborn.modules.movement;

import insane96mcp.iguanatweaksreborn.modules.movement.feature.NoPillaringFeature;
import insane96mcp.iguanatweaksreborn.modules.movement.feature.WeightedArmorFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Movement")
public class MovementModule extends Module {

	public WeightedArmorFeature weightedArmorFeature;
	public NoPillaringFeature noPillaringFeature;

	public MovementModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		weightedArmorFeature = new WeightedArmorFeature(this);
		noPillaringFeature = new NoPillaringFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		weightedArmorFeature.loadConfig();
		noPillaringFeature.loadConfig();
	}

}
