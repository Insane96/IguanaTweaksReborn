package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.combat.Combat;
import insane96mcp.iguanatweaksreborn.module.experience.Experience;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;
import insane96mcp.iguanatweaksreborn.module.mining.Mining;
import insane96mcp.iguanatweaksreborn.module.misc.Misc;
import insane96mcp.iguanatweaksreborn.module.movement.Movement;

public class Modules {

	public static HungerHealth hungerHealth;
	public static Experience experience;
	public static Misc misc;
	public static Mining mining;
	public static Combat combat;
	public static Movement movement;

	public static void init() {
		hungerHealth = new HungerHealth();
		experience = new Experience();
		misc = new Misc();
		mining = new Mining();
		combat = new Combat();
		movement = new Movement();
	}

	public static void loadConfig() {
		hungerHealth.loadConfig();
		experience.loadConfig();
		misc.loadConfig();
		mining.loadConfig();
		combat.loadConfig();
		movement.loadConfig();
	}
}
