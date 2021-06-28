package insane96mcp.iguanatweaksreborn.modules.hungerhealth;

import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.ExaustionIncreaseFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealthModule extends Module {

	public FoodFeature food;
	public ExaustionIncreaseFeature exaustionIncrease;

	public HungerHealthModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		food = new FoodFeature(this);
		exaustionIncrease = new ExaustionIncreaseFeature(this);
		Config.builder.pop();
	}

	@Override
    public void loadConfig() {
		super.loadConfig();
		food.loadConfig();
		exaustionIncrease.loadConfig();
	}

}
