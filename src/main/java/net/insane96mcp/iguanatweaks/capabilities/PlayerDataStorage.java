package net.insane96mcp.iguanatweaks.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerDataStorage implements IStorage<IPlayerData> {

	@Override
	public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("IguanaTweaks:timestampHideHungerBar", instance.getHideHungerBarLastTimestamp());
		tags.setInteger("IguanaTweaks:timestampHideHealthBar", instance.getHideHealthBarLastTimestamp());
		tags.setInteger("IguanaTweaks:timestampHideHotbar", instance.getHideHotbarLastTimestamp());
		tags.setInteger("IguanaTweaks:timestampHideExperience", instance.getHideExperienceLastTimestamp());
		tags.setInteger("IguanaTweaks:timestampHideArmor", instance.getHideArmorLastTimestamp());
		tags.setFloat("IguanaTweaks:weight", instance.getWeight());
		tags.setInteger("IguanaTweaks:damageSlownessDuration", instance.getDamageSlownessDuration());
		return tags;
	}

	@Override
	public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound nbtTagCompound = (NBTTagCompound)nbt;
		instance.setHideHungerBarLastTimestamp(nbtTagCompound.getInteger("IguanaTweaks:timestampHideHungerBar"));
		instance.setHideHealthBarLastTimestamp(nbtTagCompound.getInteger("IguanaTweaks:timestampHideHealthBar"));
		instance.setHideHotbarLastTimestamp(nbtTagCompound.getInteger("IguanaTweaks:timestampHideHotbar"));
		instance.setHideExperienceLastTimestamp(nbtTagCompound.getInteger("IguanaTweaks:timestampHideExperience"));
		instance.setHideArmorLastTimestamp(nbtTagCompound.getInteger("IguanaTweaks:timestampHideArmor"));
		instance.setWeight(nbtTagCompound.getFloat("IguanaTweaks:weight"));
		instance.setDamageSlownessDuration(nbtTagCompound.getInteger("IguanaTweaks:damageSlownessDuration"));
	}

}
