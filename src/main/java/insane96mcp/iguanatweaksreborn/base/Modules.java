package insane96mcp.iguanatweaksreborn.base;

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
	public static MiscModule miscModule;
	public static SleepRespawnModule sleepRespawnModule;
	public static ExperienceModule experienceModule;
	public static MiningModule miningModule;
	public static CombatModule combatModule;
	public static MovementModule movementModule;
	public static HungerHealthModule hungerHealthModule;
	public static StackSizeModule stackSizeModule;
	public static FarmingModule farmingModule;

	public static void init() {
		miscModule = new MiscModule();
		sleepRespawnModule = new SleepRespawnModule();
		experienceModule = new ExperienceModule();
		miningModule = new MiningModule();
		combatModule = new CombatModule();
		movementModule = new MovementModule();
		hungerHealthModule = new HungerHealthModule();
		stackSizeModule = new StackSizeModule();
		farmingModule = new FarmingModule();
	}
}
