package insane96mcp.iguanatweaksreborn.module.mining;

import insane96mcp.iguanatweaksreborn.module.mining.feature.CustomHardness;
import insane96mcp.iguanatweaksreborn.module.mining.feature.GlobalHardness;
import insane96mcp.iguanatweaksreborn.module.mining.feature.MiningMisc;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Mining")
public class Mining extends Module {

	public GlobalHardness globalHardness;
	public CustomHardness customHardness;
	public MiningMisc miningMisc;

	public Mining() {
		super(ITCommonConfig.builder);
		pushConfig(ITCommonConfig.builder);
		globalHardness = new GlobalHardness(this);
		customHardness = new CustomHardness(this);
		miningMisc = new MiningMisc(this);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		globalHardness.loadConfig();
		customHardness.loadConfig();
		miningMisc.loadConfig();
	}
}
