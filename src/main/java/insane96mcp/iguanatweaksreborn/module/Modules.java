package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.module.combat.Combat;
import insane96mcp.iguanatweaksreborn.module.experience.Experience;
import insane96mcp.iguanatweaksreborn.module.farming.Farming;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.HungerHealth;
import insane96mcp.iguanatweaksreborn.module.mining.Mining;
import insane96mcp.iguanatweaksreborn.module.misc.Misc;
import insane96mcp.iguanatweaksreborn.module.movement.Movement;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.SleepRespawn;
import insane96mcp.iguanatweaksreborn.module.stacksize.StackSize;

public class Modules {

	public static Combat combat;
	public static Experience experience;
	public static Farming farming;
	public static HungerHealth hungerHealth;
	public static Mining mining;
	public static Misc misc;
	public static Movement movement;
	public static SleepRespawn sleepRespawn;
	public static StackSize stackSize;

	public static void init() {
		combat = new Combat();
		experience = new Experience();
		farming = new Farming();
		hungerHealth = new HungerHealth();
		mining = new Mining();
		misc = new Misc();
		movement = new Movement();
		sleepRespawn = new SleepRespawn();
		stackSize = new StackSize();
	}

	public static void loadConfig() {
		combat.loadConfig();
		experience.loadConfig();
		farming.loadConfig();
		hungerHealth.loadConfig();
		mining.loadConfig();
		misc.loadConfig();
		movement.loadConfig();
		sleepRespawn.loadConfig();
		stackSize.loadConfig();
	}

}
