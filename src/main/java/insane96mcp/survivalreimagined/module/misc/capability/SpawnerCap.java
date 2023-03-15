package insane96mcp.survivalreimagined.module.misc.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class SpawnerCap {
	public static final Capability<ISpawner> INSTANCE = CapabilityManager.get(new CapabilityToken<>() { });

	public SpawnerCap() { }
}
