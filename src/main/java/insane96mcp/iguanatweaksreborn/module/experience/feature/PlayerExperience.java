package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Player Experience", description = "Changes the experience lost on death and xp per level required.")
public class PlayerExperience extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> betterScalingLevelsConfig;
	private final ForgeConfigSpec.ConfigValue<Double> droppedExperienceOnDeathConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> pickUpFasterConfig;

	public boolean betterScalingLevels = true;
	public double droppedExperienceOnDeath = 0.80d;
	public boolean pickUpFaster = true;

	public PlayerExperience(Module module) {
		super(Config.builder, module, true);
		Config.builder.comment(this.getDescription()).push(this.getName());
		betterScalingLevelsConfig = Config.builder
				.comment("The experience required to level up will be linear insteaed of exponential like vanilla.\n" +
						"The formula used to calculate the xp required for next level is (3 * (current_level + 1))\n" +
						"Obviously incompatible with Allurement's 'Remove level Scaling'")
				.define("Better Scaling XP to next level", this.betterScalingLevels);
		droppedExperienceOnDeathConfig = Config.builder
				.comment("On death, players will drop this percentage of experience instead of max 7 levels. Setting to -1 will disable this." +
						"Due to Minecraft limitations this is incompatible with other mods that change the level scaling (e.g. Allurement's 'Remove level Scaling')")
				.defineInRange("Experience Dropped on Death", this.droppedExperienceOnDeath, -1d, 1d);
		pickUpFasterConfig = Config.builder
				.comment("Players will pick up experience faster")
				.define("Pickup XP Faster", this.pickUpFaster);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.betterScalingLevels = this.betterScalingLevelsConfig.get();
		this.droppedExperienceOnDeath = this.droppedExperienceOnDeathConfig.get();
		this.pickUpFaster = this.pickUpFasterConfig.get();
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled())
			return;

		if (event.phase != TickEvent.Phase.START)
			return;

		if (!this.pickUpFaster)
			return;

		if (event.player.takeXpDelay > 0)
			event.player.takeXpDelay--;
	}

	/**
	 * Returns -1 when the module/feature is not enabled, otherwise the calculated experience required to level up
	 */
	public int getBetterScalingLevel(int experienceLevel) {
		if (!this.isEnabled())
			return -1;

		if (!this.betterScalingLevels)
			return -1;

		return 3 * (experienceLevel + 1);
	}

	//Instead of using experienceTotal, calculate the xp from the xp bar and level since experienceTotal doesn't get updated on level consume
	private int getTotalExperience(Player player) {
		int totalExp = 0;
		for (int i = 0; i < player.experienceLevel; i++) {
			totalExp += getXpNeededForNextLevel(i);
		}
		totalExp += player.getXpNeededForNextLevel() * player.experienceProgress;
		return totalExp;
	}

	private int getXpNeededForNextLevel(int currentLevel) {
		int betterScalingXp = getBetterScalingLevel(currentLevel);
		if (betterScalingXp != -1)
			return betterScalingXp;

		if (currentLevel >= 30) {
			return 112 + (currentLevel - 30) * 9;
		} else {
			return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
		}
	}

	/**
	 * Returns -1 when the module/feature is not enabled, otherwise the experience dropped on death
	 */
	public int getExperienceOnDeath(Player player) {
		if (!this.isEnabled())
			return -1;

		if (this.droppedExperienceOnDeath == -1)
			return -1;

		if (player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !player.isSpectator())
			return -1;

		return (int) (getTotalExperience(player) * this.droppedExperienceOnDeath);
	}
}