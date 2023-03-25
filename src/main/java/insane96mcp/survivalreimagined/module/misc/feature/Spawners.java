package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.capability.SpawnerCap;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Spawners", description = "Spawners will no longer spawn mobs infinitely. Echo shards can reactivate a spawner. Monsters spawning from spawners ignore light and spawning is much faster")
@LoadFeature(module = Modules.Ids.MISC)
public class Spawners extends Feature {

	public static final ResourceLocation BLACKLISTED_SPAWNERS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "blacklisted_spawners");
	public static final ResourceLocation SPAWNER_REACTIVATOR = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "spawner_reactivator");
	public static final ResourceLocation NO_LIMIT_SPAWNER = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "spawner_reactivator");
	@Config(min = 0)
	@Label(name = "Minimum Spawnable Mobs", description = "The minimum amount of spawnable mobs (when the spawner is basically in the same position as the world spawn. The amount of spawnable mobs before deactivating is equal to the distance divided by 8 (plus this value). E.g. At 160 blocks from spawn the max spawnable mobs will be 160 / 8 + 25 = 20 + 25 = 55")
	public static Integer minSpawnableMobs = 25;
	@Config(min = 0d)
	@Label(name = "Spawnable mobs multiplier", description = "This multiplier increases the max mobs spawned.")
	public static Double spawnableMobsMultiplier = 1.0d;
	@Config
	@Label(name = "Bonus experience the farther from spawn", description = "If true, the spawner will drop more experience when broken based of distance from spawn. +100% every 1024 blocks from spawn. The multiplier from 'Experience From Blocks' Feature still applies.")
	public static Boolean bonusExperienceWhenFarFromSpawn = true;

	@Config
	@Label(name = "Ignore Light", description = "If true, monsters from spawners will spawn no matter the light level.")
	public static Boolean ignoreLight = true;

	@Config(min = 0)
	@Label(name = "Spawning speed boost", description = "How much faster spawners tick down the spawning delay.")
	public static Integer spawningSpeedBoost = 4;

	public Spawners(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onSpawnerSpawn(LivingSpawnEvent.SpecialSpawn event) {
		if (!this.isEnabled()
				|| !event.getSpawnReason().equals(MobSpawnType.SPAWNER)
				|| event.getSpawner() == null
				|| event.getSpawner().getSpawnerBlockEntity() == null)
			return;
		CompoundTag nbt = new CompoundTag();
		event.getSpawner().save(nbt);
		BlockPos spawnerPos = event.getSpawner().getSpawnerBlockEntity().getBlockPos();
		ServerLevel level = (ServerLevel) event.getLevel();
		if (!(event.getSpawner().getSpawnerBlockEntity() instanceof SpawnerBlockEntity mobSpawner)) {
			LogHelper.warn("SpawnerBlockEntity is null at %s. Some mod is giving a spawner a non SpawnerBlockEntity.".formatted(spawnerPos));
			return;
		}
		mobSpawner.getCapability(SpawnerCap.INSTANCE).ifPresent(spawnerCap -> {
			spawnerCap.addSpawnedMobs(1);
			if (Utils.isEntityInTag(event.getEntity(), BLACKLISTED_SPAWNERS))
				return;
			double distance = Math.sqrt(spawnerPos.distSqr(level.getSharedSpawnPos()));
			int maxSpawned = (int) ((minSpawnableMobs + (distance / 8d)) * spawnableMobsMultiplier);

			if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
				disableSpawner(mobSpawner);
			}
		});
	}

	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled()
				|| !Utils.isItemInTag(event.getItemStack().getItem(), SPAWNER_REACTIVATOR)
				|| event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock() != Blocks.SPAWNER)
			return;

		SpawnerBlockEntity spawner = (SpawnerBlockEntity) event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
		if (spawner == null)
			return;
		if (!isDisabled(spawner))
			return;

		event.setUseItem(Event.Result.ALLOW);
		if (!event.getEntity().getAbilities().instabuild)
			event.getItemStack().shrink(1);
		event.getEntity().swing(event.getHand(), true);
		resetSpawner(spawner);
	}

	@SubscribeEvent
	public void onBlockXPDrop(BlockEvent.BreakEvent event) {
		if (!isEnabled()
				|| !bonusExperienceWhenFarFromSpawn
				|| !event.getState().getBlock().equals(Blocks.SPAWNER))
			return;
		ServerLevel level = (ServerLevel) event.getLevel();
		double distance = Math.sqrt(event.getPos().distSqr(level.getSharedSpawnPos()));
		event.setExpToDrop((int) (event.getExpToDrop() * (1 + distance / 1024d)));
	}

	public static void onSpawnerServerTick(BaseSpawner spawner) {
		if (!(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity))
			return;
		//If the feature is disabled then reactivate disabled spawners and prevent further processing
		if (!isEnabled(Spawners.class)) {
			if (isDisabled(spawnerBlockEntity))
				enableSpawner(spawnerBlockEntity);
			return;
		}
		spawner.spawnDelay -= spawningSpeedBoost;
		//If spawnable mobs amount has changed then re-enable the spawner
		if (spawnerBlockEntity.getLevel() instanceof ServerLevel world) {
			spawnerBlockEntity.getCapability(SpawnerCap.INSTANCE).ifPresent(spawnerCap -> {
				double distance = Math.sqrt(spawnerBlockEntity.getBlockPos().distSqr(world.getSharedSpawnPos()));
				int maxSpawned = (int) ((minSpawnableMobs + (distance / 8d)) * spawnableMobsMultiplier);
				if (spawnerCap.getSpawnedMobs() < maxSpawned && isDisabled(spawnerBlockEntity)) {
					enableSpawner(spawnerBlockEntity);
				}
			});
		}
	}

	public static void onClientTick(BaseSpawner spawner) {
		if (!(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity))
			return;
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
		spawner.getCapability(SpawnerCap.INSTANCE).ifPresent(spawnerCap -> spawnerCap.setSpawnedMobs(0));
	}

	private static boolean isDisabled(SpawnerBlockEntity spawner) {
		BaseSpawner abstractSpawner = spawner.getSpawner();
		CompoundTag nbt = new CompoundTag();
		abstractSpawner.save(nbt);
		return nbt.getShort("MaxNearbyEntities") == (short) 0 && nbt.getShort("RequiredPlayerRange") == (short) 0;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !Utils.isItemInTag(event.getItemStack().getItem(), SPAWNER_REACTIVATOR))
			return;

		event.getToolTip().add(Component.translatable(Strings.Translatable.SPAWNER_REACTIVATOR).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}