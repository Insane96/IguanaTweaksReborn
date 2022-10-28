package insane96mcp.iguanatweaksreborn.module;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Module;

public class Modules {

	public static Module combat;
	public static Module experience;
	public static Module farming;
	public static Module hungerHealth;
	public static Module mining;
	public static Module misc;
	public static Module movement;
	public static Module sleepRespawn;
	public static Module stackSize;

	public static void init() {
		combat = Module.Builder.create(ITCommonConfig.builder, Ids.COMBAT, "Combat").build();
		experience = Module.Builder.create(ITCommonConfig.builder, Ids.EXPERIENCE, "Experience").build();
		farming = Module.Builder.create(ITCommonConfig.builder, Ids.FARMING, "Farming").build();
		hungerHealth = Module.Builder.create(ITCommonConfig.builder, Ids.HUNGER_HEALTH, "Hunger & Health").build();
		mining = Module.Builder.create(ITCommonConfig.builder, Ids.MINING, "Mining").build();
		misc = Module.Builder.create(ITCommonConfig.builder, Ids.MISC, "Miscellaneous").build();
		movement = Module.Builder.create(ITCommonConfig.builder, Ids.MOVEMENT, "Movement").build();
		sleepRespawn = Module.Builder.create(ITCommonConfig.builder, Ids.SLEEP_RESPAWN, "Sleep & Respawn").build();
		stackSize = Module.Builder.create(ITCommonConfig.builder, Ids.STACK_SIZE, "Stack Sizes").build();
	}

	public static class Ids {
		public static final String COMBAT = IguanaTweaksReborn.RESOURCE_PREFIX + "combat";
		public static final String EXPERIENCE = IguanaTweaksReborn.RESOURCE_PREFIX + "experience";
		public static final String FARMING = IguanaTweaksReborn.RESOURCE_PREFIX + "farming";
		public static final String HUNGER_HEALTH = IguanaTweaksReborn.RESOURCE_PREFIX + "hunger_health";
		public static final String MINING = IguanaTweaksReborn.RESOURCE_PREFIX + "mining";
		public static final String MISC = IguanaTweaksReborn.RESOURCE_PREFIX + "misc";
		public static final String MOVEMENT = IguanaTweaksReborn.RESOURCE_PREFIX + "movement";
		public static final String SLEEP_RESPAWN = IguanaTweaksReborn.RESOURCE_PREFIX + "sleep_respawn";
		public static final String STACK_SIZE = IguanaTweaksReborn.RESOURCE_PREFIX + "stack_size";
	}
}
