package insane96mcp.iguanatweaksreborn.module.misc.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISpawnerData extends INBTSerializable<CompoundTag> {
	int getSpawnedMobs();
	void addSpawnedMobs(int spawnedMobs);
	void setSpawnedMobs(int spawnedMobs);

	void setDisabled(boolean disabled);
	boolean isDisabled();
}
