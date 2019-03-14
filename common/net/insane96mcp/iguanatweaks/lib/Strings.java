package net.insane96mcp.iguanatweaks.lib;

import net.insane96mcp.iguanatweaks.IguanaTweaks;

public class Strings {
	public static class Names {
		
	}
	
	public static class Translatable {
		public static class Hardness{
			private static String name = IguanaTweaks.RESOURCE_PREFIX + "hardness.";
			public static String need_tool = name + "need_tool";
			public static String wrong_tool = name + "wrong_tool";
		}
		
		public static class MovementRestriction {
			private static String name = IguanaTweaks.RESOURCE_PREFIX + "movement_restriction.";
			public static String slightly_encumbered = name + "encumbered.slightly";
			public static String encumbered = name + "encumbered.encumbered";
			public static String almost_fully_encumbered = name + "encumbered.almost_fully";
			public static String greatly_encumbered = name + "encumbered.greatly";
			public static String fully_encumbered = name + "encumbered.fully";
			public static String weight = name + "weight";
			public static String weight_when_worn = name + "weight_when_worn";
		}
		
		public static class SleepRespawn {
			private static String name = IguanaTweaks.RESOURCE_PREFIX + "sleep_respawn.";
			public static String random_respawn = name + "random_respawn";
			public static String bed_destroyed = name + "bed_destroyed";
			public static String bed_decoration = name + "bed_decoration";
			public static String enjoy_the_night = name + "enjoy_the_night";
		}
	}
}
