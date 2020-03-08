package insane96mcp.iguanatweaksreborn.utils;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;

public class LogHelper {
    public static void Error(String format, Object... args) {
        IguanaTweaksReborn.LOGGER.error(String.format(format, args));
    }

    public static void Warn(String format, Object... args) {
        IguanaTweaksReborn.LOGGER.warn(String.format(format, args));
    }

    public static void Info(String format, Object... args) {
        IguanaTweaksReborn.LOGGER.info(String.format(format, args));
    }
}
