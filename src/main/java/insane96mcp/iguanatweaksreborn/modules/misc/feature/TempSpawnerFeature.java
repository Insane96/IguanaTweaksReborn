package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.modules.misc.capability.ISpawner;
import insane96mcp.iguanatweaksreborn.modules.misc.capability.SpawnerCapability;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Label(name = "Temporary Spawners", description = "Spawners will no longer spawn mobs infinitely")
public class TempSpawnerFeature extends Feature {

    private final ForgeConfigSpec.ConfigValue<Integer> minSpawnableMobsConfig;
    private final ForgeConfigSpec.ConfigValue<Double> spawnableMobsMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> bonusExperienceWhenFarFromSpawnConfig;
    private final BlacklistConfig entityBlacklistConfig;

    public int minSpawnableMobs = 25;
    public double spawnableMobsMultiplier = 1.0d;
    public boolean bonusExperienceWhenFarFromSpawn = true;
    public List<IdTagMatcher> entityBlacklist;
    public boolean entityBlacklistAsWhitelist = false;

    public TempSpawnerFeature(Module module) {
        super(Config.builder, module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        minSpawnableMobsConfig = Config.builder
                .comment("The minimum amount of spawnable mobs (when the spawner is basically in the same position as the world spawn. The amount of spawnable mobs before deactivating is equal to the distance divided by 8 (plus this value). E.g. At 160 blocks from spawn the max spawnable mobs will be 160 / 8 + 25 = 20 + 25 = 55")
                .defineInRange("Minimum Spawnable Mobs", minSpawnableMobs, 0, Integer.MAX_VALUE);
        spawnableMobsMultiplierConfig = Config.builder
                .comment("This multiplier increases the max mobs spawned.")
                .defineInRange("Spawnable mobs multiplier", spawnableMobsMultiplier, 0d, Double.MAX_VALUE);
        bonusExperienceWhenFarFromSpawnConfig = Config.builder
                .comment("If true, the spawner will drop more experience when broken based of distance from spawn. +100% every 1024 blocks from spawn. The multiplier from 'Experience From Blocks' Feature still applies.")
                .define("Bonus experience the farther from spawn", bonusExperienceWhenFarFromSpawn);
        entityBlacklistConfig = new BlacklistConfig(Config.builder, "Entity Blacklist", "A list of mobs (and optionally dimensions) that shouldn't have their spawner disabled. Each entry has an entity or entity tag and optionally a dimension. E.g. [\"minecraft:zombie\", \"minecraft:blaze,minecraft:the_nether\"]", Collections.emptyList(), false);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.minSpawnableMobs = this.minSpawnableMobsConfig.get();
        this.spawnableMobsMultiplier = this.spawnableMobsMultiplierConfig.get();
        this.bonusExperienceWhenFarFromSpawn = this.bonusExperienceWhenFarFromSpawnConfig.get();
        this.entityBlacklist = IdTagMatcher.parseStringList(this.entityBlacklistConfig.listConfig.get());
        this.entityBlacklistAsWhitelist = this.entityBlacklistConfig.listAsWhitelistConfig.get();
    }

    @SubscribeEvent
    public void onSpawnerSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (!this.isEnabled())
            return;
        if (!event.getSpawnReason().equals(SpawnReason.SPAWNER))
            return;
        if (event.getSpawner() == null)
            return;
        CompoundNBT nbt = new CompoundNBT();
        event.getSpawner().write(nbt);
        BlockPos spawnerPos = event.getSpawner().getSpawnerPosition();
        ServerWorld world = (ServerWorld) event.getWorld();
        MobSpawnerTileEntity mobSpawner = (MobSpawnerTileEntity) world.getTileEntity(spawnerPos);
        ISpawner spawnerCap = mobSpawner.getCapability(SpawnerCapability.SPAWNER).orElse(null);
        if (spawnerCap == null)
            LogHelper.error("Something's wrong. The spawner has no capability");
        spawnerCap.addSpawnedMobs(1);
        //If it's in the black/whitelist don't disable the spawner
        Optional<EntityType<?>> optional = EntityType.readEntityType(nbt.getCompound("SpawnData"));
        if (!optional.isPresent())
            return;
        boolean isInWhitelist = false;
        boolean isInBlacklist = false;
        for (IdTagMatcher blacklistEntry : this.entityBlacklist) {
            if (blacklistEntry.matchesEntity(optional.get(), world.getDimensionKey().getLocation())) {
                if (!this.entityBlacklistAsWhitelist)
                    isInBlacklist = true;
                else
                    isInWhitelist = true;
                break;
            }
        }
        if (isInBlacklist || (!isInWhitelist && this.entityBlacklistAsWhitelist))
            return;
        double distance = Math.sqrt(spawnerPos.distanceSq(world.getSpawnPoint()));
        int maxSpawned = (int) ((this.minSpawnableMobs + (distance / 8d)) * this.spawnableMobsMultiplier);
        if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
            disableSpawner(event.getSpawner());
        }
    }

    @SubscribeEvent
    public void onBlockXPDrop(BlockEvent.BreakEvent event) {
        if (!this.isEnabled())
            return;
        if (!this.bonusExperienceWhenFarFromSpawn)
            return;
        if (!event.getState().getBlock().equals(Blocks.SPAWNER))
            return;
        ServerWorld world = (ServerWorld) event.getWorld();
        double distance = Math.sqrt(event.getPos().distanceSq(world.getSpawnPoint()));
        event.setExpToDrop((int) (event.getExpToDrop() * (1 + distance / 1024d)));
    }

    public void onTick(MobSpawnerTileEntity spawner) {
        //If the feature is disabled then reactivate disabled spawners and prevent further processing
        if (!this.isEnabled()) {
            if (isDisabled(spawner.getSpawnerBaseLogic()))
                enableSpawner(spawner.getSpawnerBaseLogic());
            return;
        }
        //If spawnable mobs amount has changed then re-enable the spawner
        if (spawner.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) spawner.getWorld();
            ISpawner spawnerCap = spawner.getCapability(SpawnerCapability.SPAWNER).orElse(null);
            if (spawnerCap == null)
                LogHelper.error("Something's wrong. The spawner has no capability");
            double distance = Math.sqrt(spawner.getPos().distanceSq(world.getSpawnPoint()));
            int maxSpawned = (int) ((this.minSpawnableMobs + (distance / 8d)) * this.spawnableMobsMultiplier);
            if (spawnerCap.getSpawnedMobs() < maxSpawned && isDisabled(spawner.getSpawnerBaseLogic())) {
                enableSpawner(spawner.getSpawnerBaseLogic());
            }
        }
        World world = spawner.getWorld();
        CompoundNBT nbt = new CompoundNBT();
        spawner.write(nbt);
        if (!isDisabled(spawner.getSpawnerBaseLogic()))
            return;
        if (world == null)
            return;
        BlockPos blockpos = spawner.getPos();
        if (!(world instanceof ServerWorld)) {
            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.SMOKE, blockpos.getX() + world.rand.nextDouble(), blockpos.getY() + world.rand.nextDouble(), blockpos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static void disableSpawner(AbstractSpawner spawner) {
        CompoundNBT nbt = new CompoundNBT();
        spawner.write(nbt);
        nbt.putShort("MaxNearbyEntities", (short) 0);
        nbt.putShort("RequiredPlayerRange", (short) 0);
        spawner.read(nbt);
    }

    private static void enableSpawner(AbstractSpawner spawner) {
        CompoundNBT nbt = new CompoundNBT();
        spawner.write(nbt);
        nbt.putShort("MaxNearbyEntities", (short) 6);
        nbt.putShort("RequiredPlayerRange", (short) 16);
        spawner.read(nbt);
    }

    private static boolean isDisabled(AbstractSpawner spawner) {
        CompoundNBT nbt = new CompoundNBT();
        spawner.write(nbt);
        return nbt.getShort("MaxNearbyEntities") == (short) 0 && nbt.getShort("RequiredPlayerRange") == (short) 0;
    }
}
