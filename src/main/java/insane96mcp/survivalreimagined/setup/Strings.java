package insane96mcp.survivalreimagined.setup;

import insane96mcp.survivalreimagined.SurvivalReimagined;

import java.util.UUID;

//TODO Remove
public class Strings {
	public static class Tags {
		public static final String SPAWNED_MOBS = SurvivalReimagined.RESOURCE_PREFIX + "spawned_mobs";

		public static final String SPAWNER_DATA = SurvivalReimagined.RESOURCE_PREFIX + "spawner_data";
        public static final String PASSIVE_REGEN_TICK = SurvivalReimagined.RESOURCE_PREFIX + "passive_regen_ticks";
		public static final String SPAWNER_DISABLED = SurvivalReimagined.RESOURCE_PREFIX + "spawner_disabled";
    }

	public static class AttributeModifiers {
		public static final UUID ARMOR_SLOWDOWN_UUID = UUID.fromString("8588420e-ce50-4e4e-a3e4-974dfc8a98ec");
		public static final String ARMOR_SLOWDOWN = SurvivalReimagined.RESOURCE_PREFIX + "armor_slowdown";
		public static final String GENERIC_ITEM_MODIFIER = SurvivalReimagined.RESOURCE_PREFIX + "item_modifier";
	}

	public static class Translatable {
		public static final String NO_FOOD_FOR_SLEEP = "survivalreimagined.no_food_for_sleep";
		public static final String DECORATIVE_BEDS = "survivalreimagined.decorative_beds";
		public static final String ENJOY_THE_NIGHT = "survivalreimagined.enjoy_the_night";

		public static final String NO_EFFICIENCY_ITEM = "survivalreimagined.no_efficiency_item";
		public static final String NO_DAMAGE_ITEM = "survivalreimagined.no_damage_item";

		public static final String SPAWNER_REACTIVATOR = SurvivalReimagined.MOD_ID + ".spawner_reactivator";
		public static final String NO_SPAWN_POINT = SurvivalReimagined.MOD_ID + ".no_spawn_point";
	}
}
