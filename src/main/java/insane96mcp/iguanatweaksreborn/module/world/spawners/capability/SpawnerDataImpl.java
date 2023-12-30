package insane96mcp.iguanatweaksreborn.module.world.spawners.capability;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.nbt.CompoundTag;

public class SpawnerDataImpl implements ISpawnerData {

    public static final String SPAWNED_MOBS = IguanaTweaksReborn.RESOURCE_PREFIX + "spawned_mobs";
    public static final String SPAWNER_DISABLED = IguanaTweaksReborn.RESOURCE_PREFIX + "spawner_disabled";
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
		nbt.putInt(SPAWNED_MOBS, this.getSpawnedMobs());
		nbt.putBoolean(SPAWNER_DISABLED, this.isDisabled());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.setSpawnedMobs(nbt.getInt(SPAWNED_MOBS));
		this.setDisabled(nbt.getBoolean(SPAWNER_DISABLED));
	}
}