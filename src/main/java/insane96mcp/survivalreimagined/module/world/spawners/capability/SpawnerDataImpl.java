package insane96mcp.survivalreimagined.module.world.spawners.capability;

import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.nbt.CompoundTag;

public class SpawnerDataImpl implements ISpawnerData {

	private int spawnedMobs;
	private boolean disabled;

	@Override
	public int getSpawnedMobs() {
		return this.spawnedMobs;
	}

	@Override
	public void addSpawnedMobs(int spawnedMobs) {
		this.spawnedMobs += spawnedMobs;
	}

	@Override
	public void setSpawnedMobs(int spawnedMobs) {
		this.spawnedMobs = spawnedMobs;
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public boolean isDisabled() {
		return this.disabled;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(Strings.Tags.SPAWNED_MOBS, this.getSpawnedMobs());
		nbt.putBoolean(Strings.Tags.SPAWNER_DISABLED, this.isDisabled());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.setSpawnedMobs(nbt.getInt(Strings.Tags.SPAWNED_MOBS));
		this.setDisabled(nbt.getBoolean(Strings.Tags.SPAWNER_DISABLED));
	}
}