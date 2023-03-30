package insane96mcp.survivalreimagined.module;

import insane96mcp.insanelib.base.Module;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.setup.SRCommonConfig;
import net.minecraftforge.fml.config.ModConfig;

public class Modules {

	public static Module combat;
	public static Module experience;
	public static Module farming;
	public static Module hungerHealth;
	public static Module items;
	public static Module mining;
	public static Module misc;
	public static Module mobs;
	public static Module movement;
	public static Module sleepRespawn;
	public static Module world;

	public static void init() {
		combat = Module.Builder.create(Ids.COMBAT, "Combat", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		experience = Module.Builder.create(Ids.EXPERIENCE, "Experience", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		farming = Module.Builder.create(Ids.FARMING, "Farming", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		hungerHealth = Module.Builder.create(Ids.HUNGER_HEALTH, "Hunger & Health", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		hungerHealth = Module.Builder.create(Ids.ITEMS, "Items", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		mining = Module.Builder.create(Ids.MINING, "Mining", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		misc = Module.Builder.create(Ids.MISC, "Miscellaneous", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		mobs = Module.Builder.create(Ids.MOBS, "Mobs", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		movement = Module.Builder.create(Ids.MOVEMENT, "Movement", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		sleepRespawn = Module.Builder.create(Ids.SLEEP_RESPAWN, "Sleep & Respawn", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
		world = Module.Builder.create(Ids.WORLD, "World", ModConfig.Type.COMMON, SRCommonConfig.builder).build();
	}

	public static class Ids {
		public static final String COMBAT = SurvivalReimagined.RESOURCE_PREFIX + "combat";
		public static final String EXPERIENCE = SurvivalReimagined.RESOURCE_PREFIX + "experience";
		public static final String FARMING = SurvivalReimagined.RESOURCE_PREFIX + "farming";
		public static final String HUNGER_HEALTH = SurvivalReimagined.RESOURCE_PREFIX + "hunger_health";
		public static final String ITEMS = SurvivalReimagined.RESOURCE_PREFIX + "hunger_health";
		public static final String MINING = SurvivalReimagined.RESOURCE_PREFIX + "mining";
		public static final String MISC = SurvivalReimagined.RESOURCE_PREFIX + "misc";
		public static final String MOBS = SurvivalReimagined.RESOURCE_PREFIX + "mobs";
		public static final String MOVEMENT = SurvivalReimagined.RESOURCE_PREFIX + "movement";
		public static final String SLEEP_RESPAWN = SurvivalReimagined.RESOURCE_PREFIX + "sleep_respawn";
		public static final String WORLD = SurvivalReimagined.RESOURCE_PREFIX + "world";
	}
}
