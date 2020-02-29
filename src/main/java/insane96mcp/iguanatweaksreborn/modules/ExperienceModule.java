package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;

public class ExperienceModule {
	public static void oreXpDrop(BlockEvent.BreakEvent event) {
		if (!ModConfig.Modules.experience.get())
			return;

		if (ModConfig.Experience.oreMultiplier.get() == 1.0d)
			return;

		int xpToDrop = event.getExpToDrop();
		IguanaTweaksReborn.LOGGER.info(xpToDrop);
		xpToDrop *= ModConfig.Experience.oreMultiplier.get(); //2.5d
		IguanaTweaksReborn.LOGGER.info(xpToDrop);
		event.setExpToDrop(xpToDrop);
	}

	public static void globalXpDrop(EntityJoinWorldEvent event) {
		if (!ModConfig.Modules.experience.get())
			return;

		if (ModConfig.Experience.globalMultiplier.get() == 1.0d)
			return;

		if (!(event.getEntity() instanceof ExperienceOrbEntity))
			return;

		ExperienceOrbEntity xpOrb = (ExperienceOrbEntity) event.getEntity();

		if (ModConfig.Experience.globalMultiplier.get() == 0d)
			xpOrb.remove();
		else
			xpOrb.xpValue *= ModConfig.Experience.globalMultiplier.get();

		if (xpOrb.xpValue == 0d)
			xpOrb.remove();
	}

	public static void mobsFromSpawnersXpDrop(LivingExperienceDropEvent event) {
		if (!ModConfig.Modules.experience.get())
			return;

		if (ModConfig.Experience.mobsFromSpawnersMultiplier.get() == 1.0d)
			return;

		LivingEntity living = event.getEntityLiving();
		CompoundNBT tags = living.getPersistentData();

		IguanaTweaksReborn.LOGGER.info("mobsFromSpawnersXpDrop");

		if (!tags.getBoolean(IguanaTweaksReborn.RESOURCE_PREFIX + "spawnedFromSpawner"))
			return;

		int xp = event.getDroppedExperience();
		xp *= ModConfig.Experience.mobsFromSpawnersMultiplier.get();
		event.setDroppedExperience(xp);
	}

	public static void checkFromSpawner(LivingSpawnEvent.CheckSpawn event) {
		IguanaTweaksReborn.LOGGER.info(event.getSpawnReason() + "");
		if (event.getSpawnReason() == SpawnReason.SPAWNER) {
			event.getEntityLiving().getPersistentData().putBoolean(IguanaTweaksReborn.RESOURCE_PREFIX + "spawnedFromSpawner", true);
		}
	}
}
