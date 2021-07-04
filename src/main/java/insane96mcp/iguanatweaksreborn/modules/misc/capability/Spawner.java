package insane96mcp.iguanatweaksreborn.modules.misc.capability;

public class Spawner implements ISpawner {

	private int spawnedMobs;

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
}
