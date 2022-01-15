package insane96mcp.iguanatweaksreborn.module.experience.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.setup.Strings;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Spawners' Mobs", description = "Decrease / Increase experience dropped mobs spawned by Spawners")
public class SpawnerMobsExperience extends Feature {
	private final ForgeConfigSpec.ConfigValue<Double> mobsFromSpawnersMultiplierConfig;

	public double mobsFromSpawnersMultiplier = 0.667d;

	public SpawnerMobsExperience(Module module) {
		super(Config.builder, module, true);
		Config.builder.comment(this.getDescription()).push(this.getName());
		mobsFromSpawnersMultiplierConfig = Config.builder
				.comment("Experience dropped from mobs that come from spawners will be multiplied by this multiplier. Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'\nCan be set to 0 to disable experience drop from mob that come from spawners.")
				.defineInRange("Mobs from Spawners Multiplier", this.mobsFromSpawnersMultiplier, 0.0d, 1000d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.mobsFromSpawnersMultiplier = this.mobsFromSpawnersMultiplierConfig.get();
	}

	@SubscribeEvent
	public void markFromSpawner(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof Mob mob))
			return;
		if (!mob.getPersistentData().getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER))
			return;

		mob.getPersistentData().putDouble(Strings.Tags.EXPERIENCE_MULTIPLIER, this.mobsFromSpawnersMultiplier);
	}
}