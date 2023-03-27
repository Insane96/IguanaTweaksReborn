package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;

import java.util.UUID;

public class Strings {
	public static class Tags {
		public static final String SPAWNED_MOBS = SurvivalReimagined.RESOURCE_PREFIX + "spawned_mobs";
		public static final String TIME_SINCE_LAST_SWING = SurvivalReimagined.RESOURCE_PREFIX + "ticks_since_last_swing";

		public static final String SPAWNER_DATA = SurvivalReimagined.RESOURCE_PREFIX + "spawner_data";
		public static final String TIREDNESS = SurvivalReimagined.RESOURCE_PREFIX + "tiredness";
		public static final String DAMAGE_HISTORY = SurvivalReimagined.RESOURCE_PREFIX + "damage_history";
		public static final String EAT_HISTORY = SurvivalReimagined.RESOURCE_PREFIX + "eat_history";
        public static final String PASSIVE_REGEN_TICK = SurvivalReimagined.RESOURCE_PREFIX + "passive_regen_ticks";
		public static final String SPAWNER_DISABLED = SurvivalReimagined.RESOURCE_PREFIX + "spawner_disabled";
    }

	public static class AttributeModifiers {
		public static final UUID ARMOR_SLOWDOWN_UUID = UUID.fromString("8588420e-ce50-4e4e-a3e4-974dfc8a98ec");
		public static final String ARMOR_SLOWDOWN = SurvivalReimagined.RESOURCE_PREFIX + "armor_slowdown";
		public static final UUID WEATHER_SLOWDOWN_UUID = UUID.fromString("81cd06ed-b645-4c96-91fe-e29e940503c3");
		public static final String WEATHER_SLOWDOWN = SurvivalReimagined.RESOURCE_PREFIX + "weather_slowdown";
		public static final UUID GENERIC_ITEM_MODIFIER_UUID = UUID.fromString("8ba29557-f4dd-449d-a1b8-396ed980d042");
		public static final String GENERIC_ITEM_MODIFIER = SurvivalReimagined.RESOURCE_PREFIX + "item_modifier";
	}

	public static class Translatable {
		public static final String NO_FOOD_FOR_SLEEP = "survivalreimagined.no_food_for_sleep";
		public static final String NOT_TIRED = "survivalreimagined.not_tired";
		public static final String TIRED_ENOUGH = "survivalreimagined.tired_enough";
		public static final String TOO_TIRED = "survivalreimagined.too_tired";
		public static final String DECORATIVE_BEDS = "survivalreimagined.decorative_beds";
		public static final String ENJOY_THE_NIGHT = "survivalreimagined.enjoy_the_night";
		public static final String ITEM_REPAIRED = "survivalreimagined.item_repaired";

		public static final String NO_EFFICIENCY_ITEM = "survivalreimagined.no_efficiency_item";
		public static final String NO_DAMAGE_ITEM = "survivalreimagined.no_damage_item";

		public static final String ARMOR_SLOWDOWN = "survivalreimagined.armor_slowdown";
		public static final String SPAWNER_REACTIVATOR = SurvivalReimagined.MOD_ID + ".spawner_reactivator";
		public static final String NO_SPAWN_POINT = SurvivalReimagined.MOD_ID + ".no_spawn_point";
	}
}
