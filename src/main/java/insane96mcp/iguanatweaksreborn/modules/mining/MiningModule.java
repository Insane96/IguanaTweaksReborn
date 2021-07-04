package insane96mcp.iguanatweaksreborn.modules.mining;

import insane96mcp.iguanatweaksreborn.modules.mining.feature.CustomHardnessFeature;
import insane96mcp.iguanatweaksreborn.modules.mining.feature.GlobalHardnessFeature;
import insane96mcp.iguanatweaksreborn.modules.mining.feature.WrongToolFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Mining")
public class MiningModule extends Module {

	public GlobalHardnessFeature globalHardness;
	public CustomHardnessFeature customHardness;
	public WrongToolFeature wrongTool;

	public MiningModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		globalHardness = new GlobalHardnessFeature(this);
		customHardness = new CustomHardnessFeature(this);
		wrongTool = new WrongToolFeature(this);
		Config.builder.pop();
	}

	@Override
    public void loadConfig() {
		super.loadConfig();
		globalHardness.loadConfig();
		customHardness.loadConfig();
		wrongTool.loadConfig();
	}
}
