package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.setup.ILStrings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Label(name = "Experience From Spawners' Mobs", description = "Decrease / Increase experience dropped mobs spawned by Spawners")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class SpawnerMobsExperience extends Feature {
	public static final ResourceLocation NO_SPAWNER_XP_MULTIPLIER = new ResourceLocation(IguanaTweaksReborn.MOD_ID + "no_spawner_xp_multiplier");
	@Config(min = 0, max = 128d)
	@Label(name = "Mobs from Spawners Multiplier", description = """
						Experience dropped from mobs that come from spawners will be multiplied by this multiplier.
						Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'
						Can be set to 0 to disable experience drop from mob that come from spawners.""")
	public static Double mobsFromSpawnersMultiplier = 0.5d;

	public SpawnerMobsExperience(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void setExperienceMultiplier(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| mobsFromSpawnersMultiplier == 1d
				|| !(event.getEntity() instanceof Mob mob)
				|| !mob.getPersistentData().getBoolean(ILStrings.Tags.SPAWNED_FROM_SPAWNER))
			return;

		TagKey<EntityType<?>> tagKey = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, NO_SPAWNER_XP_MULTIPLIER);
		if (ForgeRegistries.ENTITY_TYPES.tags().getTag(tagKey).contains(mob.getType()))
			return;

		mob.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, mobsFromSpawnersMultiplier);
	}
}