package insane96mcp.iguanatweaksreborn.module.experience;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.setup.ILStrings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Mobs Experience", description = "Decrease / Increase experience dropped by mobs")
@LoadFeature(module = Modules.Ids.EXPERIENCE, enabledByDefault = false)
public class MobsExperience extends Feature {
	public static final ResourceLocation NO_SPAWNER_XP_MULTIPLIER = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_spawner_xp_multiplier");
	@Config(min = 0, max = 128d)
	@Label(name = "Mobs from Spawners Multiplier", description = """
						Experience dropped from mobs that come from spawners will be multiplied by this multiplier.
						Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'
						Can be set to 0 to disable experience drop from mob that come from spawners.""")
	public static Double mobsFromSpawnersMultiplier = 0.6d;

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

	// In vanilla, mobs drop loot before checking if they should drop more experience due to gear, this makes them never drop more experience
	// This sets the xp reward before the loot drops (also changes the xp reward from 1~4 per equipment to 2)
	@SubscribeEvent
	public void fixEquipmentExperience(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof Mob mob)
				|| mob.xpReward <= 0)
			return;

		for (ItemStack stack : mob.getArmorSlots()) {
			if (!stack.isEmpty()) {
				mob.xpReward += 2;
				if (stack.isEnchanted())
					mob.xpReward += 2;
			}
		}
		for (ItemStack stack : mob.getHandSlots()) {
			if (!stack.isEmpty()) {
				mob.xpReward += 2;
				if (stack.isEnchanted())
					mob.xpReward += 2;
			}
		}
	}
}