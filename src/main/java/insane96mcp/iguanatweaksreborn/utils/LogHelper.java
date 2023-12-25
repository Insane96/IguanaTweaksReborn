package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;

public class LogHelper {
	public static void error(String format, Object... args) {
		IguanaTweaksReborn.LOGGER.error(String.format(format, args));
	}

	public static void warn(String format, Object... args) {
		IguanaTweaksReborn.LOGGER.warn(String.format(format, args));
	}

	public static void info(String format, Object... args) {
		IguanaTweaksReborn.LOGGER.info(String.format(format, args));
	}

	public static void debug(String format, Object... args) {
		IguanaTweaksReborn.LOGGER.debug(String.format(format, args));
	}
}
