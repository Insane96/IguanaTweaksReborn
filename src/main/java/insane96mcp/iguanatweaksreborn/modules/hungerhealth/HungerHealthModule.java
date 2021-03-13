package insane96mcp.iguanatweaksreborn.modules.hungerhealth;

import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.ExaustionIncreaseFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealthModule extends Module {

	public FoodFeature foodFeature;
	public ExaustionIncreaseFeature exaustionIncreaseFeature;

	public HungerHealthModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		foodFeature = new FoodFeature(this);
		exaustionIncreaseFeature = new ExaustionIncreaseFeature(this);
		Config.builder.pop();
	}

    @Override
    public void loadConfig() {
        super.loadConfig();
        foodFeature.loadConfig();
        exaustionIncreaseFeature.loadConfig();
    }

}
