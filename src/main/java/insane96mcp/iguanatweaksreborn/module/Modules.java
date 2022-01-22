package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.experience.Experience;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;
import insane96mcp.iguanatweaksreborn.module.mining.Mining;
import insane96mcp.iguanatweaksreborn.module.misc.Misc;

public class Modules {

	public static HungerHealth hungerHealth;
	public static Experience experience;
	public static Misc misc;
	public static Mining mining;

	public static void init() {
		hungerHealth = new HungerHealth();
		experience = new Experience();
		misc = new Misc();
		mining = new Mining();
	}

	public static void loadConfig() {
		hungerHealth.loadConfig();
		experience.loadConfig();
		misc.loadConfig();
		mining.loadConfig();
	}
}
