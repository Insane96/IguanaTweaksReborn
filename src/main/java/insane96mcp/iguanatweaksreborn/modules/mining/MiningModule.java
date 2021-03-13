package insane96mcp.iguanatweaksreborn.modules.mining;

import insane96mcp.iguanatweaksreborn.modules.mining.feature.CustomHardnessFeature;
import insane96mcp.iguanatweaksreborn.modules.mining.feature.GlobalHardnessFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Mining")
public class MiningModule extends Module {

	public GlobalHardnessFeature globalHardnessFeature;
	public CustomHardnessFeature customHardnessFeature;

	public MiningModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		globalHardnessFeature = new GlobalHardnessFeature(this);
		customHardnessFeature = new CustomHardnessFeature(this);
		Config.builder.pop();
	}

    @Override
    public void loadConfig() {
        super.loadConfig();
        globalHardnessFeature.loadConfig();
        customHardnessFeature.loadConfig();
    }
}
