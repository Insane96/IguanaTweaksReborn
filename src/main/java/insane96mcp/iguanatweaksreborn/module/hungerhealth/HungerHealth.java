package insane96mcp.iguanatweaksreborn.module.hungerhealth;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.FoodConsuming;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Hunger")
public class HungerHealth extends Module {

	public FoodConsuming foodConsuming;

	public HungerHealth() {
		super(Config.builder);
		this.pushConfig(Config.builder);
		foodConsuming = new FoodConsuming(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		foodConsuming.loadConfig();
	}
}
