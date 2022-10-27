package insane96mcp.iguanatweaksreborn.module.hungerhealth;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.ExhaustionIncrease;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.FoodConsuming;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.FoodHunger;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.HealthRegen;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealth extends Module {

	public FoodConsuming foodConsuming;
	public HealthRegen healthRegen;
	public FoodHunger foodHunger;
	public ExhaustionIncrease exhaustionIncrease;

	public HungerHealth() {
		super(ITCommonConfig.builder);
		this.pushConfig(ITCommonConfig.builder);
		foodConsuming = new FoodConsuming(this);
		healthRegen = new HealthRegen(this);
		foodHunger = new FoodHunger(this);
		exhaustionIncrease = new ExhaustionIncrease(this);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		foodConsuming.loadConfig();
		healthRegen.loadConfig();
		foodHunger.loadConfig();
		exhaustionIncrease.loadConfig();
	}
}
