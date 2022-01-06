package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;

public class Modules {

	public static HungerHealth hungerHealth;

	public static void init() {
		hungerHealth = new HungerHealth();
	}

	public static void loadConfig() {
		hungerHealth.loadConfig();
	}
}
