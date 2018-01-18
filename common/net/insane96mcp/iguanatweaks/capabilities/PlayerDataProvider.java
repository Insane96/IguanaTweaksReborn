package net.insane96mcp.iguanatweaks.capabilities;

import net.minecraft.advancements.critereon.BredAnimalsTrigger.Instance;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PlayerDataProvider implements ICapabilitySerializable<NBTBase>{
	
	@CapabilityInject(IPlayerData.class)
	public static final Capability<IPlayerData> PLAYER_DATA_CAP = null;
	
	private IPlayerData instance = PLAYER_DATA_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == PLAYER_DATA_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == PLAYER_DATA_CAP ? PLAYER_DATA_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return PLAYER_DATA_CAP.getStorage().writeNBT(PLAYER_DATA_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		PLAYER_DATA_CAP.getStorage().readNBT(PLAYER_DATA_CAP, this.instance, null, nbt);
	}
	
}
