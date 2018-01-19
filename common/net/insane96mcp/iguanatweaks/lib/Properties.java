package net.insane96mcp.iguanatweaks.lib;

import java.util.ArrayList;
import java.util.List;

public class Properties {
	
	public static void Init() {
		General.Init();
		Hardness.Init();
		StackSizes.Init();
		SleepRespawn.Init();
		Hud.Init();
		Drops.Init();
		MovementRestriction.Init();
	}
	
	public static class General{
		public static String CATEGORY = "general";
		public static String DESCRIPTION = "Other settings";
		
		public static boolean increasedStepHeight;
	    public static boolean lessObviousSilverfish;
	    public static boolean alterPoison;
	    //public static int torchesPerCoal;
		public static int tickRateEntityUpdate;
		public static boolean disableFovOnSpeedModified;
		
		public static void Init() {
			tickRateEntityUpdate = Config.LoadIntProperty(CATEGORY, "tick_rate_entity_update", "How often the speed of players are calculated (in ticks). Higher values reduce client-side CPU load but may increase the chance of odd behavior", 7);
			disableFovOnSpeedModified = Config.LoadBoolProperty(CATEGORY, "disable_fov_change_on_speed_change", "Disables fov changes when you get slowed down or sped up. Highly recommended if you have 'movement_restrictions' active.", true);
			increasedStepHeight = Config.LoadBoolProperty(CATEGORY, "increased_step_height", "If the player should be able to walk over full blocks", false);
			alterPoison = Config.LoadBoolProperty(CATEGORY, "alter_poison", "The poison effect will be changed to be deadly and drain hunger slowly, but will damage the player slowly", true);
			lessObviousSilverfish = Config.LoadBoolProperty(CATEGORY, "less_obivious_silverfish", "If true, silverfish blocks will be almost like stone", true);
			//torchesPerCoal = Config.LoadIntProperty(CATEGORY, "torches_per_coal", "Sets how many torches you get on crafting", 1);
		}
	}
	
	public static class Hardness{
		public static String CATEGORY = "hardness";
		public static String DESCRIPTION = "Change the hardness of blocks, globally or single, using either a blacklist or whitelist";
		
		public static float multiplier;
		public static boolean blockListIsWhitelist;
		public static List<String> blockList;
		
		public static List<String> blockHardness;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			multiplier = Config.LoadFloatProperty(CATEGORY, "multiplier", "Multiplier applied to the hardness of blocks (set to 1 to disable). This is applied before the block_hardness", 2.0f);
			blockListIsWhitelist = Config.LoadBoolProperty(CATEGORY, "block_list_is_whitelist", "True if hardness multiplier should only affect blocks on the list, false if all blocks are affected except those on the list", false);
			blockList = Config.LoadStringListProperty(CATEGORY, "block_list", "Block ids (one per line) for the hardness whitelist/blacklist.\nE.g. 'minecraft:stone'", new ArrayList<String>() {});
			
			blockHardness = Config.LoadStringListProperty(CATEGORY, "block_hardness", "Define for each line a custom block hardness for every block.The format is modid:blockid,hardness.\nE.g. 'minecraft:stone,5.0' will make stone have 5 hardness", new ArrayList<String>() {});
		}
	}
	
	public static class StackSizes{
		public static String CATEGORY = "stack_sizes";
		public static String DESCRIPTION = "Change the stack sizes of blocks and items, based on material weight";
		
		public static boolean logChanges;
		public static int blockDividerMin;
		public static int blockDividerMax;
		public static int itemDivider;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			logChanges = Config.LoadBoolProperty(CATEGORY, "log_changes", "If true, writes in log files any change to stack sizes", false);
			blockDividerMin = Config.LoadIntProperty(CATEGORY, "block_divider_min", "Min stack size divider for blocks", 2);
			blockDividerMax = Config.LoadIntProperty(CATEGORY, "block_divider_man", "Max stack size divider for blocks", 4);
			itemDivider = Config.LoadIntProperty(CATEGORY, "item_divider", "Stack size divider for items", 2);
		}
	}
	
	public static class SleepRespawn{
		public static String CATEGORY = "sleep_respawn";
		public static String DESCRIPTION = "Various settings to change sleeping and respawning mechanics";
		
		public static boolean disableSleeping;
		public static boolean destroyBedOnRespawn;
		public static boolean disableSetRespawnPoint;
		public static int spawnLocationRandomMin;
		public static int spawnLocationRandomMax;
		public static int respawnLocationRandomMin;
		public static int respawnLocationRandomMax;
		public static int respawnHealth;
		public static boolean respawnHealthDifficultyScaling;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			disableSleeping = Config.LoadBoolProperty(CATEGORY, "disable_sleeping", "Stops players from sleeping", true);
			destroyBedOnRespawn = Config.LoadBoolProperty(CATEGORY, "destroy_bed_on_respawn", "Upon respawn the bed is destroyed", false);
			disableSetRespawnPoint = Config.LoadBoolProperty(CATEGORY, "disable_set_respawn_point", "If active using a bed will not set your spawn point (requires disable_sleeping to be true)", false);
			
			spawnLocationRandomMin = Config.LoadIntProperty(CATEGORY, "spawn_location_random_min", "Exactly where you spawn (upon login) is randomised around the spawn point, at least a minimum of this value, many blocks away (set to 0 to disable)", 0);
			spawnLocationRandomMax = Config.LoadIntProperty(CATEGORY, "spawn_location_random_max", "Exactly where you spawn (upon login) is randomised around the spawn point, at least a maximum of this value, many blocks away (set to 0 to disable)", 0);
			respawnLocationRandomMin = Config.LoadIntProperty(CATEGORY, "respawn_location_random_min", "Where you respawn (after death) is randomised around the players' spawn point (either to a bed or original spawn point), at least a minimum of this value, many blocks away (set to 0 to disable)", 0);
			respawnLocationRandomMax = Config.LoadIntProperty(CATEGORY, "respawn_location_random_max", "Where you respawn (after death) is randomised around the players' spawn point (either to a bed or original spawn point), at least a maximum of this value, many blocks away (set to 0 to disable)", 0);

			respawnHealth = Config.LoadIntProperty(CATEGORY, "respawn_health", "Amount of health you respawn with (with 'respawnHealthDifficultyScaling' this will be modified by difficulty)", 10);
			respawnHealthDifficultyScaling = Config.LoadBoolProperty(CATEGORY, "respawn_health_difficulty_scaling", "If true, the amount of health you respawn with is dependant on difficulty", true);
			
		}
	}
	
	public static class Hud{
		public static String CATEGORY = "hud";
		public static String DESCRIPTION = "Options to hide HUD parts in certain situations";
		
		public static boolean hideHotbar;
		public static boolean hideHotbarBackground;
		public static int hideHotbarDelay;
		public static boolean hideHealthBar;
		public static int hideHealthBarThreshold;
		public static int hideHealthBarDelay;
		public static boolean hideHungerBar;
		public static int hideHungerBarThreshold;
		public static int hideHungerBarDelay;
		public static boolean hideExperienceBar;
		public static boolean showCreativeText;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			hideHotbar = Config.LoadBoolProperty(CATEGORY, "hide_hotbar", "If true, the hotbar will be hidden until an item is selected", false);
			hideHotbarDelay = Config.LoadIntProperty(CATEGORY, "hide_hotbar_delay", "Delay (in seconds) before hiding the hotbar", 3);
			
			hideHealthBar = Config.LoadBoolProperty(CATEGORY, "hide_health_bar", "If true, the health bar will be hidden when above a certain threshold (the bar will always be shown if absorpion hearts are present)", false);
			hideHealthBarThreshold = Config.LoadIntProperty(CATEGORY, "hide_health_bar_threshold", "Health needs to be equal to or above this before the bar will hide", 20);
			hideHealthBarDelay = Config.LoadIntProperty(CATEGORY, "hide_health_bar_delay", "Delay (in seconds) before hiding the health bar", 5);
			
			hideHungerBar = Config.LoadBoolProperty(CATEGORY, "hide_hunger_bar", "If true, the hunger bar will be hidden when above a certain threshold", false);
			hideHungerBarThreshold = Config.LoadIntProperty(CATEGORY, "hide_hunger_bar_threshold", "Hunger needs to be equal to or above this before the bar will hide", 20);
			hideHungerBarDelay = Config.LoadIntProperty(CATEGORY, "hide_hunger_bar_delay", "Delay (in seconds) before hiding the hunger bar", 5);
			
			hideExperienceBar = Config.LoadBoolProperty(CATEGORY, "hide_experience_bar", "If true, the experience bar will be hidden", false);
			
			showCreativeText = Config.LoadBoolProperty(CATEGORY, "show_creative_text", "If true, a 'Creative mode' text will show up when in creative mode", true);
		}
	}
	
	public static class Drops{
		public static String CATEGORY = "drops";
		public static String DESCRIPTION = "Restrict items dropped and set lifespan of items spawned in various situations (20 ticks = 1 second)";
		
		public static List<String> restrictedDrops;
		public static int itemLifespan;
		public static int itemLifespanMobDeath;
		public static int itemLifespanPlayerDeath;
		public static int itemLifespanTossed;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			restrictedDrops = Config.LoadStringListProperty(CATEGORY, "restricted_drops", "List of items/blocks to restrict from mob drops (separated by new line, format id:meta)", new ArrayList<String>() {});
			
			itemLifespan = Config.LoadIntProperty(CATEGORY, "item_lifespan", "Lifespan (in ticks) of items on the ground", 6000);
			itemLifespanMobDeath = Config.LoadIntProperty(CATEGORY, "item_lifespan_mob_drop", "Lifespan (in ticks) of items dropped when a mob dies (default 6000)", 6000);
			itemLifespanPlayerDeath = Config.LoadIntProperty(CATEGORY, "item_lifespan_player_death", "Lifespan (in ticks) of items dropped when a player dies", Integer.MAX_VALUE);
			itemLifespanTossed = Config.LoadIntProperty(CATEGORY, "item_lifespan_tossed", "Lifespan (in ticks) of items tossed on the ground", 6000);
		}
	}
	
	public static class MovementRestriction{
		public static String CATEGORY = "movement_restriction";
		public static String DESCRIPTION = "Various settings related to restricting movement, such us encumbrance, armor weight and terrain slowdown";
		
	    public static boolean addEncumbranceDebugText;
	    public static boolean addEncumbranceHudText;
	    public static boolean detailedEncumbranceHudText;
	    public static int maxCarryWeight;
		public static float rockWeight;
		public static float armorWeight;
		public static int damageSlowdownDuration;
		public static float damageSlowdownEffectiveness;
		public static boolean damageSlowdownDifficultyScaling;
		public static float terrainSlowdownPercentage;
		public static float terrainSlowdownOnDirt;
		public static float terrainSlowdownOnIce;
		public static float terrainSlowdownOnPlant;
		public static float terrainSlowdownOnSand;
		public static float terrainSlowdownOnSnow;
		public static float terrainSlowdownInSnow;
		public static float terrainSlowdownInPlant;
		
		public static void Init() {
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);
			
			addEncumbranceDebugText = Config.LoadBoolProperty(CATEGORY, "add_debug_text", "Shows weight text in the debug (F3) details", false);
			addEncumbranceHudText = Config.LoadBoolProperty(CATEGORY, "add_hud_text", "Shows weight text on the HUD when carrying too much", true);
			detailedEncumbranceHudText = Config.LoadBoolProperty(CATEGORY, "detailed_hud_text", "Weight text on the HUD will be more detailed, showing numbers", false);
			maxCarryWeight = Config.LoadIntProperty(CATEGORY, "max_carry_weight", "Maximum carry weight (set to 0 to disable)", 512);
			rockWeight = Config.LoadFloatProperty(CATEGORY, "rock_weight", "Weight of one rock block, used as a base to calculate weight of other blocks", 1);
			armorWeight = Config.LoadFloatProperty(CATEGORY, "armor_weight", "Percentage of slowdown for each point (half-shield) of armor (set to 0 to disable)", 0.5f);
			damageSlowdownDuration = Config.LoadIntProperty(CATEGORY, "damage_slowdown_duration", "Number of ticks each heart of damage slows you down for (set to 0 to disable)", 5);
			damageSlowdownEffectiveness = Config.LoadFloatProperty(CATEGORY, "damage_slowdown_effectiveness", "When player's damaged, how much is slowed down?", 20.0f);
			damageSlowdownDifficultyScaling = Config.LoadBoolProperty(CATEGORY, "damage_slowdown_difficulty_scaling", "Is the duration of the slowdown dependant on difficulty?", true);
			terrainSlowdownPercentage = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_percentage", "Global modifier on the amount that terrain affects movement speed (set to 0 to disable)", 100.0f);
			terrainSlowdownOnDirt = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_dirt", "Percentage of slowdown when walking on dirt or grass (set to 0 to disable)", 5f);
			terrainSlowdownOnIce = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_ice", "Percentage of slowdown when walking on ice (set to 0 to disable)", 20f);
			terrainSlowdownOnPlant = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_plant", "Percentage of slowdown when walking on leaves or plants (set to 0 to disable)", 20f);
			terrainSlowdownOnSand = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_sand", "Percentage of slowdown when walking on sand (set to 0 to disable)", 20f);
			terrainSlowdownOnSnow = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_snow", "Percentage of slowdown when walking on snow (set to 0 to disable)", 20f);
			terrainSlowdownInPlant = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_in_plant", "Percentage of slowdown when walking through leaves or plants (set to 0 to disable)", 5f);
			terrainSlowdownInSnow = Config.LoadFloatProperty(CATEGORY, "terrain_slowdown_in_snow", "Percentage of slowdown when walking through snow (set to 0 to disable)", 20f);
		}
	}
	
	public static class Experience{
		public static String CATEGORY = "experience";
		public static String DESCRIPTION = "Configure some properties for the vanilla experience";

		public static float percentageOre;
		public static float percentageAll;
		public static int lifespan;

		public static void Init(){
			Config.SetCategoryComment(CATEGORY, DESCRIPTION);

			percentageOre = Config.LoadFloatProperty(CATEGORY, "percentage_ore", "Percentage of experience dropped by blocks (0 to disable blocks dropping xp) (100 to disable)", 100);
			percentageAll = Config.LoadFloatProperty(CATEGORY, "percentage_all", "Percentage of experience given by orbs (0 to disable all xp orbs from being created) (100 to disable)", 100);
			lifespan = Config.LoadIntProperty(CATEGORY, "xp_lifespan", "Lifespan (in ticks) of xp orbs (Range: 0 - 38000. If set to 39000 the orbs will never despawn)", 6000);
		}
	}
	
	
}
