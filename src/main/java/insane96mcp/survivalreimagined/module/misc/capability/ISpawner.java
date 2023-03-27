package insane96mcp.survivalreimagined.module.misc.capability;

public interface ISpawner {
	int getSpawnedMobs();
	void addSpawnedMobs(int spawnedMobs);
	void setSpawnedMobs(int spawnedMobs);

	void setDisabled(boolean disabled);
	boolean isDisabled();
}
