package insane96mcp.iguanatweaksreborn.modules.hungerhealth;

import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.ExhaustionIncreaseFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FasterConsumingFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.HealthRegenFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealthModule extends Module {

	public FoodFeature food;
	public HealthRegenFeature healthRegen;
	public ExhaustionIncreaseFeature exhaustionIncrease;
	public FasterConsumingFeature fasterConsuming;

	public HungerHealthModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		food = new FoodFeature(this);
		exhaustionIncrease = new ExhaustionIncreaseFeature(this);
		healthRegen = new HealthRegenFeature(this);
		fasterConsuming = new FasterConsumingFeature(this);
		Config.builder.pop();
	}

	@Override
    public void loadConfig() {
		super.loadConfig();
		food.loadConfig();
		exhaustionIncrease.loadConfig();
		healthRegen.loadConfig();
		fasterConsuming.loadConfig();
	}

}
