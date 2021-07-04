package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.modules.misc.capability.ISpawner;
import insane96mcp.iguanatweaksreborn.modules.misc.capability.SpawnerCapability;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Temporary Spawners", description = "Spawners will no longer spawn mobs infinitely")
public class TempSpawnerFeature extends Feature {

    private final ForgeConfigSpec.ConfigValue<Integer> minSpawnableMobsConfig;
    private final ForgeConfigSpec.ConfigValue<Double> spawnableMobsMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> bonusExperienceWhenFarFromSpawnConfig;

    public int minSpawnableMobs = 25;
    public double spawnableMobsMultiplier = 1.0d;
    public boolean bonusExperienceWhenFarFromSpawn = true;

    public TempSpawnerFeature(Module module) {
        super(Config.builder, module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        minSpawnableMobsConfig = Config.builder
                .comment("The minimum amount of spawnable mobs (when the spawner is basically in the same position as the world spawn.")
                .defineInRange("Spawnable mobs multiplier", minSpawnableMobs, 0, Integer.MAX_VALUE);
        spawnableMobsMultiplierConfig = Config.builder
                .comment("This multiplier increases the max mobs spawned.")
                .defineInRange("Spawnable mobs multiplier", spawnableMobsMultiplier, 0d, Double.MAX_VALUE);
        bonusExperienceWhenFarFromSpawnConfig = Config.builder
                .comment("If true, the spawner will drop more experience when broken based of distance from spawn. +100% every 1024 blocks from spawn.")
                .define("Bonus experience the farther from spawn", bonusExperienceWhenFarFromSpawn);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.minSpawnableMobs = this.minSpawnableMobsConfig.get();
        this.spawnableMobsMultiplier = this.spawnableMobsMultiplierConfig.get();
    }

    @SubscribeEvent
    public void onSpawnerSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (!this.isEnabled())
            return;
        if (!event.getSpawnReason().equals(SpawnReason.SPAWNER))
            return;
        BlockPos spawnerPos = event.getSpawner().getSpawnerPosition();
        ServerWorld world = (ServerWorld) event.getWorld();
        MobSpawnerTileEntity mobSpawner = (MobSpawnerTileEntity) world.getTileEntity(spawnerPos);
        ISpawner spawnerCap = mobSpawner.getCapability(SpawnerCapability.SPAWNER).orElse(null);
        if (spawnerCap == null)
            LogHelper.error("Something's wrong. The spawner has no capability");
        spawnerCap.addSpawnedMobs(1);
        double distance = Math.sqrt(spawnerPos.distanceSq(world.getSpawnPoint()));
        int maxSpawned = (int) ((this.minSpawnableMobs + (distance / 8d)) * this.spawnableMobsMultiplier);
        if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
            CompoundNBT nbt = new CompoundNBT();
            event.getSpawner().write(nbt);
            nbt.putShort("MaxNearbyEntities", (short) 0);
            nbt.putShort("RequiredPlayerRange", (short) 0);
            event.getSpawner().read(nbt);
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
}
