package insane96mcp.survivalreimagined.module.world.spawners;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.world.spawners.capability.ISpawnerData;
import insane96mcp.survivalreimagined.module.world.spawners.capability.SpawnerData;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.SpawnerStatusSyncMessage;
import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

@Label(name = "Spawners", description = "Spawners will no longer spawn mobs infinitely. Echo shards can reactivate a spawner. Monsters spawning from spawners ignore light and spawning is much faster")
@LoadFeature(module = Modules.Ids.WORLD)
public class Spawners extends Feature {

	public static final ResourceLocation BLACKLISTED_SPAWNERS = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "blacklisted_spawners");
	public static final TagKey<Item> SPAWNER_REACTIVATOR_TAG = SRItemTagsProvider.create("spawner_reactivator");
	public static final String SPAWNER_REACTIVATOR = SurvivalReimagined.MOD_ID + ".spawner_reactivator";
	@Config(min = 0)
	@Label(name = "Minimum Spawnable Mobs", description = "The minimum amount of spawnable mobs (when the spawner is basically in the same position as the world spawn. The amount of spawnable mobs before deactivating is equal to the distance divided by 8 (plus this value). E.g. At 160 blocks from spawn the max spawnable mobs will be 160 / 8 + 25 = 20 + 25 = 55")
	public static Integer minSpawnableMobs = 20;
	@Config(min = 0d)
	@Label(name = "Spawnable mobs multiplier", description = "This multiplier increases the max mobs spawned.")
	public static Double spawnableMobsMultiplier = 1.0d;
	@Config
	@Label(name = "Bonus experience if not disabled", description = "If true, the spawner will drop more experience when broken, if not disabled, based of distance from spawn. +100% every 1024 blocks from spawn. The multiplier from 'Experience From Blocks' Feature still applies.")
	public static Boolean bonusExperienceIfNotDisabled = true;

	@Config
	@Label(name = "Ignore Light", description = "If true, monsters from spawners will spawn no matter the light level.")
	public static Boolean ignoreLight = true;

	@Config(min = 0)
	@Label(name = "Spawning speed boost", description = "How much faster spawners tick down the spawning delay.")
	public static Integer spawningSpeedBoost = 3;

	public Spawners(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onSpawnerSpawn(MobSpawnEvent.FinalizeSpawn event) {
		if (!this.isEnabled()
				|| !event.getSpawnType().equals(MobSpawnType.SPAWNER)
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
		mobSpawner.getCapability(SpawnerData.INSTANCE).ifPresent(spawnerCap -> {
			spawnerCap.addSpawnedMobs(1);
			if (Utils.isEntityInTag(event.getEntity(), BLACKLISTED_SPAWNERS))
				return;
			double distance = Math.sqrt(spawnerPos.distSqr(level.getSharedSpawnPos()));
			int maxSpawned = (int) ((minSpawnableMobs + (distance / 8d)) * spawnableMobsMultiplier);

			if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
				setSpawnerStatus(mobSpawner, true);
			}
			mobSpawner.setChanged();
		});
	}

	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
		if (!Utils.isItemInTag(event.getItemStack().getItem(), SPAWNER_REACTIVATOR_TAG)
				|| event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock() != Blocks.SPAWNER)
			return;

		SpawnerBlockEntity spawner = (SpawnerBlockEntity) event.getLevel().getBlockEntity(event.getHitVec().getBlockPos());
		if (spawner == null
				|| !isDisabled(spawner))
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
				|| !bonusExperienceIfNotDisabled
				|| !event.getState().getBlock().equals(Blocks.SPAWNER))
			return;
		BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
		if (!(blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity))
			return;
		ServerLevel level = (ServerLevel) event.getLevel();
		double distance = Math.sqrt(event.getPos().distSqr(level.getSharedSpawnPos()));
		float distanceRatio = isDisabled(spawnerBlockEntity) ? 1024f : 256f;
		event.setExpToDrop((int) (event.getExpToDrop() * (1 + distance / distanceRatio)));
	}

	/**
	 * Returns true if the spawner should not tick
	 */
	public static boolean onSpawnerServerTick(BaseSpawner spawner) {
		if (!(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity))
			return false;
		if (!Feature.isEnabled(Spawners.class))
			return false;
		else if (isDisabled(spawnerBlockEntity))
			return true;
		spawner.spawnDelay = Math.max(spawner.spawnDelay - spawningSpeedBoost, 0);
		return false;
	}

	/**
	 * Returns true if the spawner should not tick
	 */
	public static boolean onSpawnerClientTick(BaseSpawner spawner) {
		if (!Feature.isEnabled(Spawners.class)
			|| !(spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity))
			return false;
		spawner.spawnDelay = Math.max(spawner.spawnDelay - spawningSpeedBoost, 0);
		if (!isDisabled(spawnerBlockEntity))
			return false;
		Level level = spawnerBlockEntity.getLevel();
		if (level == null)
			return false;
		BlockPos blockpos = spawnerBlockEntity.getBlockPos();
		for (int i = 0; i < 10; i++) {
			level.addParticle(ParticleTypes.SMOKE, blockpos.getX() + level.random.nextDouble(), blockpos.getY() + level.random.nextDouble(), blockpos.getZ() + level.random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
		return true;
	}

	private static void setSpawnerStatus(SpawnerBlockEntity spawner, boolean disabled) {
		spawner.getCapability(SpawnerData.INSTANCE).ifPresent(spawnerCap -> spawnerCap.setDisabled(disabled));
		spawner.setChanged();
		//noinspection ConstantConditions
		if (spawner.hasLevel() && !spawner.getLevel().isClientSide) {
			Object msg = new SpawnerStatusSyncMessage(spawner.getBlockPos(), disabled);
			for (Player player : spawner.getLevel().players()) {
				NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}

	private static void resetSpawner(SpawnerBlockEntity spawner) {
		setSpawnerStatus(spawner, false);
		spawner.getCapability(SpawnerData.INSTANCE).ifPresent(spawnerCap -> spawnerCap.setSpawnedMobs(0));
		spawner.setChanged();
	}

	private static boolean isDisabled(SpawnerBlockEntity spawner) {
		LazyOptional<ISpawnerData> cap = spawner.getCapability(SpawnerData.INSTANCE);
		return cap.map(ISpawnerData::isDisabled).orElse(false);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !Utils.isItemInTag(event.getItemStack().getItem(), SPAWNER_REACTIVATOR_TAG))
			return;

		event.getToolTip().add(Component.translatable(SPAWNER_REACTIVATOR).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}