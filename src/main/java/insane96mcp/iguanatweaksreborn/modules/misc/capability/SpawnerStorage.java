package insane96mcp.iguanatweaksreborn.modules.misc.capability;

import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SpawnerStorage implements Capability.IStorage<ISpawner> {

	@Nullable
	@Override
	public INBT writeNBT(Capability<ISpawner> capability, ISpawner instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(Strings.Tags.SPAWNED_MOBS, instance.getSpawnedMobs());
		return nbt;
	}

	@Override
	public void readNBT(Capability<ISpawner> capability, ISpawner instance, Direction side, INBT nbt) {
		if (!(instance instanceof Spawner))
			throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
		CompoundNBT compoundNBT = (CompoundNBT) nbt;
		instance.setSpawnedMobs(compoundNBT.getInt(Strings.Tags.SPAWNED_MOBS));
	}
}
