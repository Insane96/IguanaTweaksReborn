package insane96mcp.survivalreimagined.module.misc.capability;

public class Spawner implements ISpawner {

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
}