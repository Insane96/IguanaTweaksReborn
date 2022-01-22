package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.movement.feature.NoPillaring;
import insane96mcp.iguanatweaksreborn.module.movement.feature.Tagging;
import insane96mcp.iguanatweaksreborn.module.movement.feature.WeightedArmor;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Movement")
public class Movement extends Module {

	public WeightedArmor weightedArmor;
	public NoPillaring noPillaring;
	public Tagging tagging;

	public Movement() {
		super(Config.builder);
		pushConfig(Config.builder);
		weightedArmor = new WeightedArmor(this);
		noPillaring = new NoPillaring(this);
		tagging = new Tagging(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		weightedArmor.loadConfig();
		noPillaring.loadConfig();
		tagging.loadConfig();
	}
}
