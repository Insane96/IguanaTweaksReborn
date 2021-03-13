package insane96mcp.iguanatweaksreborn.modules.mining;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.mining.feature.CustomHardnessFeature;
import insane96mcp.iguanatweaksreborn.modules.mining.feature.GlobalHardnessFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Mining")
public class MiningModule extends ITModule {

    public GlobalHardnessFeature globalHardnessFeature;
    public CustomHardnessFeature customHardnessFeature;

    public MiningModule() {
        super();
        pushConfig();
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
