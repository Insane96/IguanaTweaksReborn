package net.insane96mcp.iguanatweaks.capabilities;

public interface IPlayerData {
	public int getHideHungerBarLastTimestamp();
	public void setHideHungerBarLastTimestamp(int timestamp);

	public int getHideHealthBarLastTimestamp();
	public void setHideHealthBarLastTimestamp(int timestamp);

	public int getHideHotbarLastTimestamp();
	public void setHideHotbarLastTimestamp(int timestamp);

	public int getHideXpBarLastTimestamp();
	public void setHideXpBarLastTimestamp(int timestamp);
	
	public float getWeight();
	public void setWeight(float weight);

	public int getDamageSlownessDuration();
	public void setDamageSlownessDuration(int duration);
	public void tickDamageSlownessDuration();
}
