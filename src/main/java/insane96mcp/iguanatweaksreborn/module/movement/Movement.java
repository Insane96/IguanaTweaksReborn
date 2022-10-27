package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.movement.feature.*;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Movement")
public class Movement extends Module {

	public WeightedEquipment weightedEquipment;
	public NoPillaring noPillaring;
	public Tagging tagging;
	public TerrainSlowdown terrainSlowdown;
	public BackwardsSlowdown backwardsSlowdown;

	public Movement() {
		super(ITCommonConfig.builder);
		pushConfig(ITCommonConfig.builder);
		weightedEquipment = new WeightedEquipment(this);
		noPillaring = new NoPillaring(this);
		tagging = new Tagging(this);
		terrainSlowdown = new TerrainSlowdown(this);
		backwardsSlowdown = new BackwardsSlowdown(this);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		weightedEquipment.loadConfig();
		noPillaring.loadConfig();
		tagging.loadConfig();
		terrainSlowdown.loadConfig();
		backwardsSlowdown.loadConfig();
	}
}
