package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.movement.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Movement")
public class Movement extends Module {

	public WeightedArmor weightedArmor;
	public NoPillaring noPillaring;
	public Tagging tagging;
	public TerrainSlowdown terrainSlowdown;
	public BackwardsSlowdown backwardsSlowdown;

	public Movement() {
		super(Config.builder);
		pushConfig(Config.builder);
		weightedArmor = new WeightedArmor(this);
		noPillaring = new NoPillaring(this);
		tagging = new Tagging(this);
		terrainSlowdown = new TerrainSlowdown(this);
		backwardsSlowdown = new BackwardsSlowdown(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		weightedArmor.loadConfig();
		noPillaring.loadConfig();
		tagging.loadConfig();
		terrainSlowdown.loadConfig();
		backwardsSlowdown.loadConfig();
	}
}
