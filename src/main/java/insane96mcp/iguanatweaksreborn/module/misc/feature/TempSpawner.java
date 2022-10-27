package insane96mcp.iguanatweaksreborn.module.misc.feature;

import insane96mcp.iguanatweaksreborn.module.misc.capability.ISpawner;
import insane96mcp.iguanatweaksreborn.module.misc.capability.SpawnerCap;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.Blacklist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;

@Label(name = "Temporary Spawners", description = "Spawners will no longer spawn mobs infinitely")
public class TempSpawner extends Feature {

	private final ForgeConfigSpec.ConfigValue<Integer> minSpawnableMobsConfig;
	private final ForgeConfigSpec.ConfigValue<Double> spawnableMobsMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> bonusExperienceWhenFarFromSpawnConfig;
	private final ForgeConfigSpec.ConfigValue<String> reagentItemConfig;
	private final Blacklist.Config entityBlacklistConfig;

	public int minSpawnableMobs = 25;
	public double spawnableMobsMultiplier = 1.0d;
	public boolean bonusExperienceWhenFarFromSpawn = true;
	public Item reagentItem = null;
	public Blacklist entityBlacklist;

	public TempSpawner(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		minSpawnableMobsConfig = ITCommonConfig.builder
				.comment("The minimum amount of spawnable mobs (when the spawner is basically in the same position as the world spawn. The amount of spawnable mobs before deactivating is equal to the distance divided by 8 (plus this value). E.g. At 160 blocks from spawn the max spawnable mobs will be 160 / 8 + 25 = 20 + 25 = 55")
				.defineInRange("Minimum Spawnable Mobs", minSpawnableMobs, 0, Integer.MAX_VALUE);
		spawnableMobsMultiplierConfig = ITCommonConfig.builder
				.comment("This multiplier increases the max mobs spawned.")
				.defineInRange("Spawnable mobs multiplier", spawnableMobsMultiplier, 0d, Double.MAX_VALUE);
		bonusExperienceWhenFarFromSpawnConfig = ITCommonConfig.builder
				.comment("If true, the spawner will drop more experience when broken based of distance from spawn. +100% every 1024 blocks from spawn. The multiplier from 'Experience From Blocks' Feature still applies.")
				.define("Bonus experience the farther from spawn", bonusExperienceWhenFarFromSpawn);
		reagentItemConfig = ITCommonConfig.builder
				.comment("Set here an item that can be used on spawners and let you re-enable them.")
				.define("Reagent Item", "");
		entityBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Entity Blacklist", "A list of mobs (and optionally dimensions) that shouldn't have their spawner disabled. Each entry has an entity or entity tag and optionally a dimension. E.g. [\"minecraft:zombie\", \"minecraft:blaze,minecraft:the_nether\"]")
				.setDefaultList(Collections.emptyList())
				.setIsDefaultWhitelist(false)
				.build();
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.minSpawnableMobs = this.minSpawnableMobsConfig.get();
		this.spawnableMobsMultiplier = this.spawnableMobsMultiplierConfig.get();
		this.bonusExperienceWhenFarFromSpawn = this.bonusExperienceWhenFarFromSpawnConfig.get();

		ResourceLocation rl = ResourceLocation.tryParse(this.reagentItemConfig.get());
		if (rl != null && ForgeRegistries.ITEMS.containsKey(rl))
			this.reagentItem = ForgeRegistries.ITEMS.getValue(rl);
		else if (!this.reagentItemConfig.get().equals(""))
			LogHelper.warn("Reagent item %s not valid or does not exist", this.reagentItemConfig.get());

		this.entityBlacklist = this.entityBlacklistConfig.get();
	}

	@SubscribeEvent
	public void onSpawnerSpawn(LivingSpawnEvent.SpecialSpawn event) {
		if (!this.isEnabled())
			return;
		if (!event.getSpawnReason().equals(MobSpawnType.SPAWNER))
			return;
		if (event.getSpawner() == null || event.getSpawner().getSpawnerBlockEntity() == null)
			return;
		CompoundTag nbt = new CompoundTag();
		event.getSpawner().save(nbt);
		BlockPos spawnerPos = event.getSpawner().getSpawnerBlockEntity().getBlockPos();
		ServerLevel level = (ServerLevel) event.getWorld();
		SpawnerBlockEntity mobSpawner = (SpawnerBlockEntity) level.getBlockEntity(spawnerPos);
		ISpawner spawnerCap = mobSpawner.getCapability(SpawnerCap.INSTANCE).orElse(null);
		if (spawnerCap == null)
			LogHelper.error("Something's wrong. The spawner has no capability");
		spawnerCap.addSpawnedMobs(1);
		if (this.entityBlacklist.isEntityBlackOrNotWhitelist(event.getEntityLiving()))
			return;
		double distance = Math.sqrt(spawnerPos.distSqr(level.getSharedSpawnPos()));
		int maxSpawned = (int) ((this.minSpawnableMobs + (distance / 8d)) * this.spawnableMobsMultiplier);

		if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
			disableSpawner(mobSpawner);
		}
	}

	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled())
			return;
		if (this.reagentItem == null)
			return;
		if (!event.getItemStack().getItem().equals(this.reagentItem))
			return;

		if (event.getWorld().getBlockState(event.getHitVec().getBlockPos()).getBlock() != Blocks.SPAWNER)
			return;

		SpawnerBlockEntity spawner = (SpawnerBlockEntity) event.getWorld().getBlockEntity(event.getHitVec().getBlockPos());
		if (spawner == null)
			return;
		if (!isDisabled(spawner))
			return;

		event.setUseItem(Event.Result.ALLOW);
		event.getItemStack().shrink(1);
		event.getPlayer().swing(event.getHand(), true);
		resetSpawner(spawner);
	}

	@SubscribeEvent
	public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.bonusExperienceWhenFarFromSpawn)
			return;
		if (!event.getState().getBlock().equals(Blocks.SPAWNER))
			return;
		ServerLevel level = (ServerLevel) event.getWorld();
		double distance = Math.sqrt(event.getPos().distSqr(level.getSharedSpawnPos()));
		event.setExpToDrop((int) (event.getExpToDrop() * (1 + distance / 1024d)));
	}

	public void onServerTick(BaseSpawner spawner) {
		if (!(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity))
			return;
		SpawnerBlockEntity spawnerBlockEntity = (SpawnerBlockEntity) spawner.getSpawnerBlockEntity();
		//If the feature is disabled then reactivate disabled spawners and prevent further processing
		if (!this.isEnabled()) {
			if (isDisabled(spawnerBlockEntity))
				enableSpawner(spawnerBlockEntity);
			return;
		}
		//If spawnable mobs amount has changed then re-enable the spawner
		if (spawnerBlockEntity.getLevel() instanceof ServerLevel world) {
			ISpawner spawnerCap = spawnerBlockEntity.getCapability(SpawnerCap.INSTANCE).orElse(null);
			if (spawnerCap == null)
				LogHelper.error("Something's wrong. The spawner has no capability");
			double distance = Math.sqrt(spawnerBlockEntity.getBlockPos().distSqr(world.getSharedSpawnPos()));
			int maxSpawned = (int) ((this.minSpawnableMobs + (distance / 8d)) * this.spawnableMobsMultiplier);
			if (spawnerCap.getSpawnedMobs() < maxSpawned && isDisabled(spawnerBlockEntity)) {
				enableSpawner(spawnerBlockEntity);
			}
		}
	}

	public void onClientTick(BaseSpawner spawner) {
		if (!(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity))
			return;
		SpawnerBlockEntity spawnerBlockEntity = (SpawnerBlockEntity) spawner.getSpawnerBlockEntity();
		Level level = spawnerBlockEntity.getLevel();
		if (level == null)
			return;
		if (!isDisabled(spawnerBlockEntity))
			return;
		BlockPos blockpos = spawnerBlockEntity.getBlockPos();
		for (int i = 0; i < 10; i++) {
			level.addParticle(ParticleTypes.SMOKE, blockpos.getX() + level.random.nextDouble(), blockpos.getY() + level.random.nextDouble(), blockpos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	private static void disableSpawner(SpawnerBlockEntity spawner) {
		BaseSpawner abstractSpawner = spawner.getSpawner();
		CompoundTag nbt = new CompoundTag();
		abstractSpawner.save(nbt);
		nbt.putShort("MaxNearbyEntities", (short) 0);
		nbt.putShort("RequiredPlayerRange", (short) 0);
		spawner.load(nbt);
	}

	private static void enableSpawner(SpawnerBlockEntity spawner) {
		BaseSpawner abstractSpawner = spawner.getSpawner();
		CompoundTag nbt = new CompoundTag();
		abstractSpawner.save(nbt);
		nbt.putShort("MaxNearbyEntities", (short) 6);
		nbt.putShort("RequiredPlayerRange", (short) 16);
		spawner.load(nbt);
	}

	private static void resetSpawner(SpawnerBlockEntity spawner) {
		enableSpawner(spawner);
		ISpawner spawnerCap = spawner.getCapability(SpawnerCap.INSTANCE).orElse(null);
		if (spawnerCap == null)
			LogHelper.error("Something's wrong. The spawner has no capability");
		spawnerCap.setSpawnedMobs(0);
	}

	private static boolean isDisabled(SpawnerBlockEntity spawner) {
		BaseSpawner abstractSpawner = spawner.getSpawner();
		CompoundTag nbt = new CompoundTag();
		abstractSpawner.save(nbt);
		return nbt.getShort("MaxNearbyEntities") == (short) 0 && nbt.getShort("RequiredPlayerRange") == (short) 0;
	}
}