package net.insane96mcp.iguanatweaks.integration;

import net.insane96mcp.iguanatweaks.IguanaTweaks;

public class Integration {
	public static void Init() {
		IguanaTweaks.logger.info("Initializing Mod Integration");
		
		BetterWithMods.Init();
		WearableBackpacks.Init();
		
		IguanaTweaks.logger.info("Finished Initializing Mod Integration");
	}
}
