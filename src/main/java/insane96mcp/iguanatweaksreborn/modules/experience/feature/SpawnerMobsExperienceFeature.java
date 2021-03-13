package insane96mcp.iguanatweaksreborn.modules.experience.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Experience From Spawners' Mobs", description = "Decrease (or Increase) experience dropped mobs spawned by Spawners")
public class SpawnerMobsExperienceFeature extends Feature {

	public static final String SPAWNED_FROM_SPANWER = IguanaTweaksReborn.RESOURCE_PREFIX + "spawnedFromSpawner";

	private final ForgeConfigSpec.ConfigValue<Double> mobsFromSpawnersMultiplierConfig;

	public double mobsFromSpawnersMultiplier = 0.667d;

	public SpawnerMobsExperienceFeature(Module module) {
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
    public void onXPDropSpawnerMobs(LivingExperienceDropEvent event) {
        if (!this.isEnabled())
            return;
        if (this.mobsFromSpawnersMultiplier == 1.0d)
            return;

        LivingEntity living = event.getEntityLiving();
        CompoundNBT tags = living.getPersistentData();
        if (!tags.getBoolean(SPAWNED_FROM_SPANWER))
            return;
        int xp = event.getDroppedExperience();
        xp *= this.mobsFromSpawnersMultiplier;
        event.setDroppedExperience(xp);
    }

    @SubscribeEvent
    public void markFromSpawner(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawnReason() == SpawnReason.SPAWNER)
            event.getEntityLiving().getPersistentData().putBoolean(SPAWNED_FROM_SPANWER, true);
    }
}
