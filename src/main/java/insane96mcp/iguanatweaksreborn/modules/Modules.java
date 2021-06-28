package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.modules.combat.CombatModule;
import insane96mcp.iguanatweaksreborn.modules.experience.ExperienceModule;
import insane96mcp.iguanatweaksreborn.modules.farming.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.HungerHealthModule;
import insane96mcp.iguanatweaksreborn.modules.mining.MiningModule;
import insane96mcp.iguanatweaksreborn.modules.misc.MiscModule;
import insane96mcp.iguanatweaksreborn.modules.movement.MovementModule;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.SleepRespawnModule;
import insane96mcp.iguanatweaksreborn.modules.stacksize.StackSizeModule;

public class Modules {
	public static MiscModule misc;
	public static SleepRespawnModule sleepRespawn;
	public static ExperienceModule experience;
	public static MiningModule mining;
	public static CombatModule combat;
	public static MovementModule movement;
	public static HungerHealthModule hungerHealth;
	public static StackSizeModule stackSize;
	public static FarmingModule farming;

	public static void init() {
		misc = new MiscModule();
		sleepRespawn = new SleepRespawnModule();
		experience = new ExperienceModule();
		mining = new MiningModule();
		combat = new CombatModule();
		movement = new MovementModule();
		hungerHealth = new HungerHealthModule();
		stackSize = new StackSizeModule();
		farming = new FarmingModule();
	}

	public static void loadConfig() {
		misc.loadConfig();
		sleepRespawn.loadConfig();
		experience.loadConfig();
		mining.loadConfig();
		combat.loadConfig();
		movement.loadConfig();
		hungerHealth.loadConfig();
		stackSize.loadConfig();
		farming.loadConfig();
	}
}
