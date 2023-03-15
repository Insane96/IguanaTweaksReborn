package insane96mcp.survivalreimagined.utils;

import insane96mcp.survivalreimagined.SurvivalReimagined;

public class LogHelper {
	public static void error(String format, Object... args) {
		SurvivalReimagined.LOGGER.error(String.format(format, args));
	}

	public static void warn(String format, Object... args) {
		SurvivalReimagined.LOGGER.warn(String.format(format, args));
	}

	public static void info(String format, Object... args) {
		SurvivalReimagined.LOGGER.info(String.format(format, args));
	}
}
