package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Player Experience", description = "Changes the experience lost on death and xp per level required.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class PlayerExperience extends Feature {
	//TODO Change this into a formula
	@Config
	@Label(name = "Better Scaling XP to next level", description = """
						The experience required to level up will be linear instead of exponential like vanilla.
						The formula used to calculate the xp required for next level is (3 * (current_level + 1))
						Obviously incompatible with Allurement's 'Remove level Scaling'""")
	public static Boolean betterScalingLevels = true;
	@Config(min = -1d, max = 1d)
	@Label(name = "Dropped Experience on Death", description = """
						On death, players will drop this percentage of experience instead of max 7 levels. Setting to -1 will disable this.
						Due to Minecraft limitations this is incompatible with other mods that change the level scaling (e.g. Allurement's 'Remove level Scaling').""")
	public static Double droppedExperienceOnDeath = 0.8d;
	@Config(min = -1d, max = 1d)
	@Label(name = "Pickup XP Faster", description = "Players will pick up experience faster")
	public static Boolean pickUpFaster = true;

	public PlayerExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (!this.isEnabled()
				|| event.phase != TickEvent.Phase.START
				|| !pickUpFaster) return;

		if (event.player.takeXpDelay > 0)
			event.player.takeXpDelay--;
	}

	/**
	 * Returns -1 when the module/feature is not enabled, otherwise the calculated experience required to level up
	 */
	public static int getBetterScalingLevel(int experienceLevel) {
		if (!isEnabled(PlayerExperience.class)
				|| !betterScalingLevels)
			return -1;

		return 3 * (experienceLevel + 1);
	}

	//Instead of using experienceTotal, calculate the xp from the xp bar and level since experienceTotal doesn't get updated on level consume
	private static int getTotalExperience(Player player) {
		int totalExp = 0;
		for (int i = 0; i < player.experienceLevel; i++) {
			totalExp += getXpNeededForNextLevel(i);
		}
		totalExp += player.getXpNeededForNextLevel() * player.experienceProgress;
		//Take into account global experience to prevent XP duping
		if (Feature.isEnabled(GlobalExperience.class) && GlobalExperience.globalMultiplier != 1d)
			totalExp *= (1d / GlobalExperience.globalMultiplier);
		if (totalExp > 250000)
			totalExp = 250000;
		return totalExp;
	}

	private static int getXpNeededForNextLevel(int currentLevel) {
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
	public static int getExperienceOnDeath(Player player) {
		if (!isEnabled(PlayerExperience.class)
				|| (droppedExperienceOnDeath < 0 && !GlobalExperience.isEnabled(GlobalExperience.class))
				|| (player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !player.isSpectator()))
			return -1;

		return (int) (getTotalExperience(player) * droppedExperienceOnDeath);
	}
}