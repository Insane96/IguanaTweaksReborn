package insane96mcp.iguanatweaksreborn.modules.hungerhealth;

import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.ExhaustionIncreaseFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodConsumingFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.FoodHungerFeature;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature.HealthRegenFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealthModule extends Module {

	public FoodHungerFeature foodHunger;
	public HealthRegenFeature healthRegen;
	public ExhaustionIncreaseFeature exhaustionIncrease;
	public FoodConsumingFeature foodConsuming;

	public HungerHealthModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		foodHunger = new FoodHungerFeature(this);
		exhaustionIncrease = new ExhaustionIncreaseFeature(this);
		healthRegen = new HealthRegenFeature(this);
		foodConsuming = new FoodConsumingFeature(this);
		Config.builder.pop();
	}

	@Override
    public void loadConfig() {
		super.loadConfig();
		foodHunger.loadConfig();
		exhaustionIncrease.loadConfig();
		healthRegen.loadConfig();
		foodConsuming.loadConfig();
	}

}
