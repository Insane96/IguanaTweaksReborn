package insane96mcp.iguanatweaksreborn.module.world.spawners.capability;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.nbt.CompoundTag;

public class SpawnerDataImpl implements ISpawnerData {

    public static final String SPAWNED_MOBS = IguanaTweaksReborn.RESOURCE_PREFIX + "spawned_mobs";
	public static final String SPAWNER_DISABLED = IguanaTweaksReborn.RESOURCE_PREFIX + "spawner_disabled";
	public static final String SPAWNER_EMPOWERED = IguanaTweaksReborn.RESOURCE_PREFIX + "spawner_empowered";
    private int spawnedMobs;
	private boolean disabled;
	private boolean empowered = true;

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
	public void setEmpowered(boolean isEmpowered) {
		this.empowered = isEmpowered;
	}

	@Override
	public boolean isEmpowered() {
		return this.empowered;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(SPAWNED_MOBS, this.getSpawnedMobs());
		nbt.putBoolean(SPAWNER_DISABLED, this.isDisabled());
		nbt.putBoolean(SPAWNER_EMPOWERED, this.isEmpowered());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.setSpawnedMobs(nbt.getInt(SPAWNED_MOBS));
		this.setDisabled(nbt.getBoolean(SPAWNER_DISABLED));
		this.setEmpowered(nbt.getBoolean(SPAWNER_EMPOWERED));
	}
}