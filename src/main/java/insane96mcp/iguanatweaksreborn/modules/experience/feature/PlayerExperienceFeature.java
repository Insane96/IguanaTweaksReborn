package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Player Experience", description = "Changes the experience lost on death and xp per level required.")
public class PlayerExperienceFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> betterScalingLevelsConfig;
	private final ForgeConfigSpec.ConfigValue<Double> droppedExperienceOnDeathConfig;

	public boolean betterScalingLevels = true;
	public double droppedExperienceOnDeath = 0.80d;

	public PlayerExperienceFeature(Module module) {
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
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.betterScalingLevels = this.betterScalingLevelsConfig.get();
		this.droppedExperienceOnDeath = this.droppedExperienceOnDeathConfig.get();
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
	private int getTotalExperience(PlayerEntity player) {
		int totalExp = 0;
		for (int i = 0; i < player.experienceLevel; i++) {
			totalExp += getXpNeededForNextLevel(i);
		}
		totalExp += player.xpBarCap() * player.experience;
		return totalExp;
	}

	private int getXpNeededForNextLevel(int currentLevel) {
		int betterScalingXp = getBetterScalingLevel(currentLevel);
		if (betterScalingXp != -1)
			return betterScalingXp;
		else {
			if (currentLevel >= 30) {
				return 112 + (currentLevel - 30) * 9;
			} else {
				return currentLevel >= 15 ? 37 + (currentLevel - 15) * 5 : 7 + currentLevel * 2;
			}
		}
	}

	/**
	 * Returns -1 when the module/feature is not enabled, otherwise the experience dropped on death
	 */
	public int getExperienceOnDeath(PlayerEntity player) {
		if (!this.isEnabled())
			return -1;

		if (this.droppedExperienceOnDeath == -1)
			return -1;

		if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator())
			return -1;

		return (int) (getTotalExperience(player) * this.droppedExperienceOnDeath);
	}
}
