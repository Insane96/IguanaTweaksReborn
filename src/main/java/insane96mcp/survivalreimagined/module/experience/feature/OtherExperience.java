package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;

@Label(name = "Other Experience", description = "Change other experience sources")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class OtherExperience extends Feature {
	@Config(min = 0, max = 512)
	@Label(name = "Bottle o' Enchanting Bonus XP", description = "Bottle o' enchanting will drop this more XP. Experience is still affected by 'Global Experience Multiplier'\nCan be set to 0 to make Bottle o' enchanting drop no bonus experience")
	public static Integer xpBottleBonus = 18;
	@Config(min = 0)
	@Label(name = "Anvil Repair Cap", description = "Set the cap for repairing items in the anvil (vanilla is 40)")
	public static Integer anvilRepairCap = 1024;
	@Config
	@Label(name = "Remove rename cost", description = "Removes cost of renaming items in Anvil")
	public static Boolean freeRenaming = true;

	public OtherExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static void onXpBottleHit(ThrownExperienceBottle xpBottle) {
		if (!isEnabled(OtherExperience.class)
				|| xpBottleBonus == 0)
			return;

		if (xpBottle.level instanceof ServerLevel) {
			ExperienceOrb.award((ServerLevel)xpBottle.level, xpBottle.position(), xpBottleBonus);
		}
	}

	public static boolean isFreeRenaming() {
		return isEnabled(OtherExperience.class) && freeRenaming;
	}
}