package net.insane96mcp.iguanatweaks.capabilities;

import net.insane96mcp.iguanatweaks.lib.Properties;

public class PlayerData implements IPlayerData{

	private int hungerBarLastTimeStamp;
	@Override
	public int getHideHungerBarLastTimestamp() {
		return this.hungerBarLastTimeStamp;
	}

	@Override
	public void setHideHungerBarLastTimestamp(int timestamp) {
		this.hungerBarLastTimeStamp = timestamp;
	}

	private int healthBarLastTimeStamp;
	@Override
	public int getHideHealthBarLastTimestamp() {
		return this.healthBarLastTimeStamp;
	}

	@Override
	public void setHideHealthBarLastTimestamp(int timestamp) {
		this.healthBarLastTimeStamp = timestamp;
	}

	private int hotbarLastTimestamp;
	@Override
	public int getHideHotbarLastTimestamp() {
		return this.hotbarLastTimestamp;
	}

	@Override
	public void setHideHotbarLastTimestamp(int timestamp) {
		this.hotbarLastTimestamp = timestamp;
	}

	private int experienceLastTimestamp;
	@Override
	public int getHideExperienceLastTimestamp() {
		return this.experienceLastTimestamp;
	}

	@Override
	public void setHideExperienceLastTimestamp(int timestamp) {
		this.experienceLastTimestamp = timestamp;
	}

	private int armorLastTimestamp;
	@Override
	public int getHideArmorLastTimestamp() {
		return this.armorLastTimestamp;
	}

	@Override
	public void setHideArmorLastTimestamp(int timestamp) {
		this.armorLastTimestamp = timestamp;
	}

	private float weight;
	@Override
	public float getWeight() {
		return this.weight;
	}

	@Override
	public void setWeight(float weight) {
		this.weight = weight;
	}

	private int damageSlownessDuration;
	@Override
	public int getDamageSlownessDuration() {
		return this.damageSlownessDuration;
	}

	@Override
	public void setDamageSlownessDuration(int duration) {
		this.damageSlownessDuration = duration;
	}

	@Override
	public void tickDamageSlownessDuration() {
		if (this.damageSlownessDuration > 0)
			this.damageSlownessDuration -= Properties.config.misc.tickRatePlayerUpdate;
	}

}
