package insane96mcp.iguanatweaksreborn.modules.misc.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnerCapability implements ICapabilitySerializable<CompoundNBT> {

	@CapabilityInject(ISpawner.class)
	public static final Capability<ISpawner> SPAWNER = null;
	private final LazyOptional<ISpawner> instance = LazyOptional.of(SPAWNER::getDefaultInstance);

	public static void register() {
		CapabilityManager.INSTANCE.register(ISpawner.class, new SpawnerStorage(), Spawner::new);
	}

	public void invalidate() {
		instance.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return SPAWNER.orEmpty(cap, instance);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return (CompoundNBT) SPAWNER.getStorage().writeNBT(SPAWNER, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		SPAWNER.getStorage().readNBT(SPAWNER, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional cannot be empty!")), null, nbt);
	}
}
