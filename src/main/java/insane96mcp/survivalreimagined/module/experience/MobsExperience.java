package insane96mcp.survivalreimagined.module.experience;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Mobs Experience", description = "Decrease / Increase experience dropped by mobs")
@LoadFeature(module = Modules.Ids.EXPERIENCE, enabledByDefault = false)
public class MobsExperience extends Feature {
	public static final ResourceLocation NO_SPAWNER_XP_MULTIPLIER = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "no_spawner_xp_multiplier");
	@Config(min = 0, max = 128d)
	@Label(name = "Mobs from Spawners Multiplier", description = """
						Experience dropped from mobs that come from spawners will be multiplied by this multiplier.
						Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'
						Can be set to 0 to disable experience drop from mob that come from spawners.""")
	public static Double mobsFromSpawnersMultiplier = 1d;

	@Config(min = 0, max = 128d)
	@Label(name = "Natural Mobs Multiplier", description = """
						Experience dropped from mobs that DON'T come from spawners will be multiplied by this multiplier.
						Experience dropped from mobs that DON'T come from spawners is still affected by 'Global Experience Multiplier'
						Can be set to 0 to disable experience drop from mob that DON'T come from spawners.""")
	public static Double naturalMobsMultiplier = 1d;

	public MobsExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void setExperienceMultiplier(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| (mobsFromSpawnersMultiplier == 1d && naturalMobsMultiplier == 1d)
				|| !(event.getEntity() instanceof Mob mob)
				|| Utils.isEntityInTag(mob, NO_SPAWNER_XP_MULTIPLIER))
			return;

		if (mob.getPersistentData().getBoolean(ILStrings.Tags.SPAWNED_FROM_SPAWNER))
			mob.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, mobsFromSpawnersMultiplier);
		else
			mob.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, naturalMobsMultiplier);
	}
}