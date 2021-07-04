package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Player Experience", description = "Changes the experience lost on death and xp per level required.")
public class PlayerExperienceFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> betterScalingLevelsConfig;
	private final ForgeConfigSpec.ConfigValue<Double> droppedExperienceOnDeathConfig;

	public boolean betterScalingLevels = true;
	public double droppedExperienceOnDeath = 0.5d;

	public PlayerExperienceFeature(Module module) {
		super(Config.builder, module, true);
		Config.builder.comment(this.getDescription()).push(this.getName());
		betterScalingLevelsConfig = Config.builder
				.comment("Instead of using the vanilla formula, the experience required to level up will be less exponential.\n" +
						"The formula used to calculate the xp required for next level is (3 * (current_level + 1))")
				.define("Better Scaling XP to next level", this.betterScalingLevels);
		droppedExperienceOnDeathConfig = Config.builder
				.comment("On death, players will drop this percentage of experience instead of max 7 levels. Setting to -1 will disable this.")
				.defineInRange("Experience Dropped on Death", this.droppedExperienceOnDeath, -1d, 1d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.betterScalingLevels = this.betterScalingLevelsConfig.get();
		this.droppedExperienceOnDeath = this.droppedExperienceOnDeathConfig.get();
	}
}
