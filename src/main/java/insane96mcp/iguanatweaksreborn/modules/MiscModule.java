package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.SpawnReason;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class MiscModule {

	public static void markFromSpawner(LivingSpawnEvent.CheckSpawn event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER)
			event.getEntityLiving().getPersistentData().putBoolean(Strings.NBTTags.SPAWNED_FROM_SPANWER, true);
	}
}
