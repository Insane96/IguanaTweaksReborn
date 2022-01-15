package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.experience.Experience;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;

public class Modules {

	public static HungerHealth hungerHealth;
	public static Experience experience;

	public static void init() {
		hungerHealth = new HungerHealth();
		experience = new Experience();
	}

	public static void loadConfig() {
		hungerHealth.loadConfig();
		experience.loadConfig();
	}
}
