package insane96mcp.iguanatweaksreborn.modules.movement;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.movement.feature.NoPillaringFeature;
import insane96mcp.iguanatweaksreborn.modules.movement.feature.WeightedArmorFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Movement")
public class MovementModule extends ITModule {

	public WeightedArmorFeature weightedArmorFeature;
	public NoPillaringFeature noPillaringFeature;

	public MovementModule() {
		super();
		Config.builder.comment(this.getDescription()).push(this.getName());
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
