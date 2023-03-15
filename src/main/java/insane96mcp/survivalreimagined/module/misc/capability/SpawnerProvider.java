package insane96mcp.survivalreimagined.module.misc.capability;

import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnerProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

	public static final ResourceLocation IDENTIFIER = new ResourceLocation(Strings.Tags.SPAWNER_CAP);

	private final ISpawner backend = new Spawner();
	private final LazyOptional<ISpawner> optionalData = LazyOptional.of(() -> backend);

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return SpawnerCap.INSTANCE.orEmpty(cap, this.optionalData);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(Strings.Tags.SPAWNED_MOBS, backend.getSpawnedMobs());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		backend.setSpawnedMobs(nbt.getInt(Strings.Tags.SPAWNED_MOBS));
	}
}
