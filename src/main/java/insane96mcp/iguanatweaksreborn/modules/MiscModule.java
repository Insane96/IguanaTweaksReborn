package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.SpawnReason;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class MiscModule {

	@OnlyIn(Dist.CLIENT)
	public static void muffleMobsFromSpawners(PlaySoundEvent event) {
		/*if (!ModConfig.Modules.misc)
			return;
		if (!ModConfig.Misc.muffleMobsFromSpawnersSounds)
			return;

		ClientWorld world = Minecraft.getInstance().world;

		if (world == null)
			return;

		ISound sound = event.getResultSound();
		if (sound instanceof ITickableSound)
			return;

		List<LivingEntity> list = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(sound.getX() - 0.1f, sound.getY() - 0.1f, sound.getZ() - 0.1f, sound.getX() + 0.1f, sound.getY() + 0.1f, sound.getZ() + 0.1f));
		for (LivingEntity entity : list) {
			CompoundNBT nbt = entity.getPersistentData();
			System.out.println(nbt);
			if (nbt.getBoolean(Strings.NBTTags.SPAWNED_FROM_SPANWER)) {
				float volume = 0.1f;
				event.setResultSound(new SoundMuffler(sound, volume));
			}
		}*/
	}

	public static void markFromSpawner(LivingSpawnEvent.CheckSpawn event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER)
			event.getEntityLiving().getPersistentData().putBoolean(Strings.NBTTags.SPAWNED_FROM_SPANWER, true);
	}
}
