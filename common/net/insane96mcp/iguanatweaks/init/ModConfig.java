package net.insane96mcp.iguanatweaks.init;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ModConfig {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec SPEC;
	
	public static void Init(Path file) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(file)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        SPEC.setConfig(configData);
	}
	
	public static class Global {
		public static String name = "global";

		public static ConfigValue<Boolean> drops;
		public static ConfigValue<Boolean> experience;
		public static ConfigValue<Boolean> hardness;
		public static ConfigValue<Boolean> hud;
		public static ConfigValue<Boolean> movementRestriction;
		public static ConfigValue<Boolean> sleepRespawn;
		public static ConfigValue<Boolean> stackSize;
		
		public static void Init() {
			BUILDER.push(name);
			drops = BUILDER
				.comment("Set to false to disable everything from the Drops module")
				.define("drops", true);
			experience = BUILDER
				.comment("Set to false to disable everything from the Experience module")
				.define("experience", true);
			hardness = BUILDER
				.comment("Set to false to disable everything from the Hardness module")
				.define("hardness", true);
			hud = BUILDER
				.comment("Set to false to disable everything from the Hud module")
				.define("hud", true);
			movementRestriction = BUILDER
				.comment("Set to false to disable everything from the Movement Restriction module")
				.define("movement_restriction", true);
			sleepRespawn = BUILDER
				.comment("Set to false to disable everything from the Sleep Respawn module")
				.define("sleep_respawn", true);
			stackSize = BUILDER
				.comment("Set to false to disable everything from the Stack Size module")
				.define("stack_size", true);
			BUILDER.pop();
		}
	}

	public static class Misc{
		public static String name = "misc";

		public static ConfigValue<Boolean> alterPoison;
		public static ConfigValue<Integer> tickRatePlayerUpdate;
		public static ConfigValue<Integer> tickRateEntityUpdate;
		public static ConfigValue<Boolean> preventFoVChangesOnSpeedModified;
		public static ConfigValue<Boolean> exhaustionOnBlockBreak;
		public static ConfigValue<Double> exhaustionMultiplier;
		public static ConfigValue<Boolean> noItemNoKnockback;
		
		public static void Init() {
			BUILDER.push(name);
			alterPoison = BUILDER
				.comment("If true, the poison effect will be changed to be deadly and drain hunger, but will damage the player 3 times slower")
				.define("alter_poison", true);
			tickRatePlayerUpdate = BUILDER
				.comment("How often the speed of players are calculated (in ticks). Higher values might increase performance but may increase the chance of odd behavior")
				.defineInRange("tick_rate_player_update", 2, 1, 20);
			tickRateEntityUpdate = BUILDER
				.comment("How often the speed of entities (not players) are calculated (in ticks). Higher values might increase performance but may increase the chance of odd behavior")
				.defineInRange("tick_rate_entity_update", 7, 1, 20);
			preventFoVChangesOnSpeedModified = BUILDER
				.comment("Disables FoV changes when you get slowed down or sped up. Highly recommended if you have Movement Restriction module active.")
				.define("prevent_fov_changes_on_speed_modified", true);
			exhaustionOnBlockBreak = BUILDER
				.comment("Minecraft normally adds 0.005 exaustion for block broken. With this at true, exhaustion will be added based on block hardness (hardness / 100). ELI5 when you break a block you lose more hunger the harder is a block to break.")
				.define("exahustion_on_block_break", true);
			exhaustionMultiplier = BUILDER
				.comment("Multiply the exhaustion given to the player when breaking blocks by this value")
				.defineInRange("exhaustion_multiplier", 1.0f, 0.0f, 64f);
			noItemNoKnockback = BUILDER
				.comment("If the player attacks an entity without a tool / weapon, the attacked mob will take almost no damage (1/10 of a heart) and no knockback. The no knockback applies even if the player attack speed cooldown is below 75% with a tool / weapon, but in this case the damage is applied normally.\nThis feature might not work properly with modded mobs")
				.define("no_item_no_knockback", true);
			BUILDER.pop();
		}
	}
	
	public static class Hardness {
		public static String name = "hardness";
		
		public static ConfigValue<Boolean> punishWrongTool;
		public static ConfigValue<Double> multiplier;
		public static ConfigValue<Boolean> blacklistIsWhitelist;
		public static ConfigValue<List<? extends String>> blacklist;
		public static ConfigValue<List<? extends String>> blocksHardness;
		
		public static void Init() {
			BUILDER.push(name);
			punishWrongTool = BUILDER
				.comment("True if the tool should break down when mining the wrong block (e.g. mining Wood with a Pickaxe or mining Obsidian with an Iron Pickaxe) or if the player has instead no tool in hand he will be damaged based on the block hardness.")
				.define("punish_wrong_tool", false);
			multiplier = BUILDER
				.comment("Multiplier applied to the hardness of blocks (set to 1 to keep normal blocks hardness)")
				.defineInRange("multiplier", 4.0f, 0f, 128f);
			blacklistIsWhitelist = BUILDER
				.comment("True if hardness multiplier should only affect blocks on the list, false if all blocks are affected except those on the list")
				.define("blacklist_is_whitelist", false);
			blacklist = BUILDER
				.comment("Block ids (one per line) for the hardness whitelist/blacklist.\nFormat is modid:blockid\\nE.g. 'minecraft:granite' will target granite")
				.defineList("blacklist", Arrays.asList(""), o -> o instanceof String);
			blocksHardness = BUILDER
				.comment("Define for each line a custom block hardness for every block. Those blocks are not affected by the global block hardness multiplier\nThe format is modid:blockid,hardness.\nE.g. 'minecraft:granite,10.0' will make granite have 10 hardness. \nBy default this is set to make ores harder to mine the better they are (accounting 4x global hardness too)")
				.defineList("blocks_hardness", Arrays.asList(
					"minecraft:coal_ore,12.0",
			        "minecraft:iron_ore,16.0",
			        "minecraft:gold_ore,20.0",
			        "minecraft:diamond_ore,24.0",
			        "minecraft:redstone_ore,16.0",
			        "minecraft:lapis_ore,14.0",
			        "minecraft:emerald_ore,28.0",
			        "minecraft:nether_quartz_ore,14.0"
				), o -> o instanceof String);
		}
	}
	
	public static class StackSizes {
		public static String name = "stack_sizes";
		
		public static ConfigValue<Integer> blockDividerMin;
		public static ConfigValue<Integer> blockDividerMax;
		public static ConfigValue<Integer> itemDivider;
		public static ConfigValue<List<? extends String>> customStackList;
		
		public static void Init() {
			BUILDER.push(name);

			blockDividerMin = BUILDER
				.comment("Min stack size divider for blocks")
				.defineInRange("block_divider_min", 2, 1, 64);
			blockDividerMax = BUILDER
				.comment("Max stack size divider for blocks")
				.defineInRange("block_divider_max", 4, 1, 64);
			itemDivider = BUILDER
				.comment("Items stack size divider")
				.defineInRange("item_divider", 2, 1, 64);
			customStackList = BUILDER
				.comment("List of all the custom stacks for blocks and items. The format is 'modid:itemid,max_stack_size'. Going over 64 doesn't work. By default, some items are set so that villagers can trade them")
				.defineList("custom_stack_list", Arrays.asList(
					"minecraft:emerald,64",
					"minecraft:paper,36",
					"minecraft:rotten_flesh,40"
				), o -> o instanceof String);
			BUILDER.pop();
		}
	}
	
	static {
		Global.Init();
		Misc.Init();
		Hardness.Init();
		StackSizes.Init();
		SPEC = BUILDER.build();
	}
	/*
		
		@Name("Sleep & Respawn")
		public SleepRespawn sleepRespawn = new SleepRespawn();
		
		public static class SleepRespawn {
			@Name("Disable Sleeping")
			@Comment("Prevents players from sleeping")
			public boolean disableSleeping = true;
			@Name("Destroy Bed on Respawn")
			@Comment("As the player respawns the bed will be destroyed. This makes bed one time respawn only")
			public boolean destroyBedOnRespawn = false;
			@Name("Disable Set Respawn Point")
			@Comment("If active using a bed will not set your spawn point (requires disable_sleeping to be true)")
			public boolean disableSetRespawnPoint = false;
			@Name("Random Spawn Location Radius Min")
			@Comment("Upon entering the world your spawn will be randomised around the spawn point, at least at this minimum distance (set to 0 to disable)")
			@RangeInt(min = 0)
			public int spawnLocationRandomMin = 0;
			@Name("Random Spawn Location Radius Max")
			@Comment("Upon entering the world your spawn will be randomised around the spawn point, at most at this maximum distance (set to 0 to disable)")
			@RangeInt(min = 0)
			public int spawnLocationRandomMax = 0;
			@Name("Random Respawn Location Radius Min")
			@Comment("Upon respawning your location will be randomised around your respawn point, at least at this minimum distance (set to 0 to disable)")
			@RangeInt(min = 0)
			public int respawnLocationRandomMin = 0;
			@Name("Random Respawn Location Radius Max")
			@Comment("Upon respawning your location will be randomised around your respawn point, at most at this maximum distance (set to 0 to disable)")
			@RangeInt(min = 0)
			public int respawnLocationRandomMax = 0;
			@Name("Respawn Health")
			@Comment("Amount of health you respawn with (with 'respawnHealthDifficultyScaling' this will be modified by difficulty)")
			@RangeInt(min = 1)
			public int respawnHealth = 10;
			@Name("Respawn Health Difficulty Based")
			@Comment("If true, the amount of health you respawn with is dependant on difficulty. (Easy x2, Normal x1, Hard x0.5)")
			public boolean respawnHealthDifficultyScaling = true;
		}

	
		public Hud hud = new Hud();
		
		public static class Hud {
			@Name("Hide Hotbar")
			@Comment("If true, the hotbar will be hidden until the mouse wheel is used or an item is selected with numbers")
			public boolean hideHotbar = false;
			@Name("Hide Hotbar Delay")
			@Comment("Delay (in seconds) before hiding the hotbar")
			@RangeInt(min = 0)
			public int hideHotbarDelay = 4;
			@Name("Hide Health bar")
			@Comment("If true, the health bar will be hidden when above a certain threshold (the bar will always be shown if absorpion hearts are present)")
			public boolean hideHealthBar = true;
			@Name("Hide Health Bar Threshold")
			@Comment("Health needs to be equal to or above this before the bar will hide")
			@RangeInt(min = 1)
			public int hideHealthBarThreshold = 20;
			@Name("Hide Health Bar Delay")
			@Comment("Delay (in seconds) before hiding the hunger bar")
			@RangeInt(min = 0)
			public int hideHealthBarDelay = 4;
			@Name("Hide Hunger Bar")
			@Comment("If true, the hunger bar will be hidden when above a certain threshold")
			public static boolean hideHungerBar = true;
			@Name("Hide Hunger Bar Threshold")
			@Comment("Hunger needs to be equal to or above this before the bar will hide")
			@RangeInt(min = 1)
			public static int hideHungerBarThreshold = 20;
			@Name("Hide Hunger Bar Delay")
			@Comment("Delay (in seconds) before hiding the hunger bar")
			@RangeInt(min = 0)
			public int hideHungerBarDelay = 4;
			@Name("Hide Experience Bar")
			@Comment("If true, the experience bar will be hidden unless there are xp orbs in a small radius around the player or a gui is open")
			public boolean hideExperienceBar = true;
			@Name("Hide Experience Bar Delay")
			@Comment("Delay (in seconds) before hiding the experience bar")
			@RangeInt(min = 0)
			public int hideExperienceDelay = 4;
			@Name("Hide Armor Bar")
			@Comment("If true, the armor bar will be hidden unless the player takes damage")
			public boolean hideArmorBar = true;
			@Name("Hide Armor Bar Delay")
			@Comment("Delay (in seconds) before hiding the armor bar")
			@RangeInt(min = 0)
			public int hideArmorDelay = 4;
			@Name("Show Creative Text")
			@Comment("If true, a 'Creative mode' text will show up when in creative mode")
			public boolean showCreativeText = true;
		}
		
		
		public Drops drops = new Drops();
		
		public static class Drops {
			@Name("Restricted Drops")
			@Comment("List of items/blocks to restrict from mob drops (separated by new line, format modid:itemid:meta)")
			public String[] restrictedDrops = new String[] {};
			@Name("Item Lifespan")
			@Comment("Lifespan (in ticks) of items on the ground")
			public int itemLifespan = 6000;
			@Name("Item Lifespan Mob Death")
			@Comment("Lifespan (in ticks) of items dropped when a mob dies")
			public int itemLifespanMobDeath = 6000;
			@Name("Item Lifespan Player Death")
			@Comment("Lifespan (in ticks) of items dropped on player death")
			public int itemLifespanPlayerDeath = Integer.MAX_VALUE;
			@Name("Item Lifespan Tossed")
			@Comment("Lifespan (in ticks) of items tossed on the ground")
			public int itemLifespanTossed = 6000;
		}
	
	
		@Name("Movement Restriction")
		public MovementRestriction movementRestriction = new MovementRestriction();
		
		public static class MovementRestriction {
			@Name("Encumbrance Debug")
			@Comment("Shows weight text in the debug (F3) details")
		    public boolean addEncumbranceDebugText = true;
			@Name("Encumbrance Hud")
			@Comment("Shows weight text on the HUD when carrying too much")
		    public boolean addEncumbranceHudText = true;
			@Name("Encumbrance Top-Left")
			@Comment("Show Encumbrance Hud on Top-Left instead of Top-Right")
			public boolean encumbranceTopLeft = false;
			@Name("Detailed Encumbrance Hud")
			@Comment("Weight text on the HUD will be more detailed, showing numbers")
		    public boolean detailedEncumbranceHudText = false;
			@Name("Max Carry Weight")
			@Comment("Maximum carry weight (set to 0 to disable)")
			@RangeInt(min = 0)
		    public int maxCarryWeight = 768;
			@Name("Rock Weight")
			@Comment("Weight of one rock block, used as a base to calculate weight of other blocks")
			@RangeDouble(min = 0f, max = Float.MAX_VALUE)
			public float rockWeight = 1;
			@Name("Custom Weights")
			@Comment("Set here (one per line) block weight for each block or item. Format is 'modid:blockid:meta,weight', meta is not needed, setting no meta, means all the blocks sub-types of that block.")
			public String[] customWeight = new String[] {};
			@Name("Armor Weight")
			@Comment("Weight added by each point of armor (set to 0 to disable)")
			@RangeDouble(min = 0f, max = Float.MAX_VALUE)
			public float armorWeight = 8f;
			@Name("Armor Weight Mobs")
			@Comment("Percentage Slowdown for each armor point for Mobs (set to zero to prevent mobs from slowing down when wearing armor)")
			@RangeDouble(min = 0f, max = 5f)
			public float armorWeightMobs = 0.5f;
			@Name("Damage Slowdown Duration")
			@Comment("Number of ticks each heart of damage slows you down for (set to 0 to disable)")
			@RangeInt(min = 0)
			public int damageSlowdownDuration = 5;
			@Name("Damage Slowdown Effectiviness")
			@Comment("When player's damaged, how much is slowed down?")
			@RangeDouble(min = 0f, max = 100f)
			public float damageSlowdownEffectiveness = 20f;
			@Name("Damage Slowdown Difficulty Scaling")
			@Comment("Is the duration of the slowdown dependant on difficulty?")
			public boolean damageSlowdownDifficultyScaling = true;
			@Name("Terrain Slowdown Percentage")
			@Comment("Global modifier on the amount that terrain affects movement speed (set to 0 to disable)")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownPercentage = 10f;
			@Name("Terrain Slowdown on Dirt")
			@Comment("Percentage of slowdown when walking on dirt or grass")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownOnDirt = 5f;
			@Name("Terrain Slowdown on Ice")
			@Comment("Percentage of slowdown when walking on ice")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownOnIce = 50f;
			@Name("Terrain Slowdown on Plants")
			@Comment("Percentage of slowdown when walking on plants")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownOnPlant = 20f;
			@Name("Terrain Slowdown on sand")
			@Comment("Percentage of slowdown when walking on sand")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownOnSand = 20f;
			@Name("Terrain Slowdown on snow")
			@Comment("Percentage of slowdown when walking on snow")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownOnSnow = 20f;
			@Name("Terrain Slowdown in Snow")
			@Comment("Percentage of slowdown when walking in snow")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownInSnow = 20f;
			@Name("Terrain Slowdown in Plants")
			@Comment("Percentage of slowdown when walking in plants")
			@RangeDouble(min = 0f, max = 100f)
			public float terrainSlowdownInPlant = 5f;
			@Name("Custom Terrain Slowdown")
			@Comment("Custom list for each block that slows you down when you walk on it. Format is 'modid:blockid:meta,slowness', meta is not needed, setting no meta, means all the blocks. E.g. 'minecraft:diamond_block,75' will slowdown the player by 75% when walks on diamond block.")
			public String[] terrainSlowdownCustom = new String[] {};
			@Name("Slowdown Walking Backwards")
			@Comment("Set to false to disable the slowdown when walking backwards")
			public boolean slowdownWhenWalkingBackwards = true;
			@Name("Shulker Weight Reduction")
			@Comment("Multiplier for items weight in shulker boxes. Set this to 0 to make items in shulker boxes not count towards weight. Set this to 1 to make items in shulker boxes weight the same as they were out of the box.")
			@RangeDouble(min = 0f, max = 1f)
			public float shulkerWeightReduction = 0.75f;
			@Name("Encubrance Exhaustion per Second")
			@Comment("How much exhaustion is given to the player each second while exaustion is 100% (e.g. at 5% encumbrance the exhaustion applied to the player will be 5% of this value)")
			@RangeDouble(min = 0f, max = 1f)
			public float encumbranceExhaustionPerSecond = 0.05f;
		}
	
	
		public Experience experience = new Experience();
		
		public static class Experience {
			@Name("Percentage Ore")
			@Comment("Percentage of experience dropped by blocks. Experience dropped by blocks are still affected by 'Percentage All'; so if you have e.g. 'Percentage All' at 50, this needs to be set to 200 to make blocks drop normal experience")
			@RangeDouble(min = 0f, max = Float.MAX_VALUE)
			public float percentageOre = 100f;
			@Name("Percentage All")
			@Comment("Percentage of experience given by everything (0 to disable all xp orbs from being created) (100 to disable)")
			@RangeDouble(min = 0f, max = Float.MAX_VALUE)
			public float percentageAll = 100f;
			@Name("Percentage Mobs From Spawner")
			@Comment("Percentage of experience dropped from mobs spawned from Spawners.")
			@RangeDouble(min = 0f, max = Float.MAX_VALUE)
			public float percentageFromSpawner = 75f;
			@Name("Lifespan")
			@Comment("Lifespan (in ticks) of xp orbs (If set to -1 the orbs will never despawn. If set to 6000 orbs will not be modified)")
			@RangeInt(min = -1, max = 38767)
			public int lifespan = 6000;
		}
	}

	@Mod.EventBusSubscriber(modid = IguanaTweaks.MOD_ID)
	private static class EventHandler{
		@SubscribeEvent
	    public static void EventOnConfigChanged(OnConfigChangedEvent event)
	    {
	        if (event.getModID().equals(IguanaTweaks.MOD_ID))
	        {
	            ConfigManager.sync(IguanaTweaks.MOD_ID, Type.INSTANCE);
	        }
	    }
	    
		@SubscribeEvent
	    public static void EventPlayerLoggedIn(PlayerLoggedInEvent event) {
	    	if (event.player.world.isRemote)
	    		return;
	    	
	    	ConfigSync message = new ConfigSync();
	    	message.lessObiviousSilverfish = ModConfig.config.misc.lessObviousSilverfish;
	    	message.multiplier = ModConfig.config.hardness.multiplier;
	    	message.blockListIsWhitelist = ModConfig.config.hardness.blockListIsWhitelist;
	    	message.blockList = String.join("\r\n", ModConfig.config.hardness.blockList);
	    	message.blockHardness = String.join("\r\n", ModConfig.config.hardness.blockHardness);
	    	
	    	PacketHandler.SendToClient(message, (EntityPlayerMP) event.player);
	    }

	}*/
}