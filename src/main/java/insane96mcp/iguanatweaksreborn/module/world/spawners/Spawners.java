package insane96mcp.iguanatweaksreborn.module.world.spawners;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.ISpawnerData;
import insane96mcp.iguanatweaksreborn.module.world.spawners.capability.SpawnerData;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.network.message.SpawnerStatusSync;
import insane96mcp.iguanatweaksreborn.utils.ITRLogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
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

import java.util.ArrayList;
import java.util.List;

@Label(name = "Spawners", description = "Spawners will no longer spawn mobs infinitely. Echo shards can reactivate a spawner. Monsters spawning from spawners ignore light and spawning is much faster")
@LoadFeature(module = Modules.Ids.WORLD)
public class Spawners extends JsonFeature {

	public static final TagKey<EntityType<?>> BLACKLISTED_SPAWNERS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "blacklisted_spawners"));
	public static final TagKey<Item> SPAWNER_REACTIVATOR_TAG = ITRItemTagsProvider.create("spawner_reactivator");
	public static final String SPAWNER_REACTIVATOR = IguanaTweaksReborn.MOD_ID + ".spawner_reactivator";
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
	public static Integer spawningSpeedBoost = 2;

	public static final ArrayList<IdTagValue> FIXED_SPAWNER_SPAWNABLE_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.newId("minecraft:blaze", "minecraft:the_nether"), 64)
	));
	public static final ArrayList<IdTagValue> fixedSpawnerSpawnable = new ArrayList<>();

	public Spawners(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("spawners.json", fixedSpawnerSpawnable, FIXED_SPAWNER_SPAWNABLE_DEFAULT, IdTagValue.LIST_TYPE));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
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
			ITRLogHelper.warn("SpawnerBlockEntity is null at %s. Some mod is giving a spawner a non SpawnerBlockEntity.".formatted(spawnerPos));
			return;
		}
		mobSpawner.getCapability(SpawnerData.INSTANCE).ifPresent(spawnerCap -> {
			spawnerCap.addSpawnedMobs(1);
			if (event.getEntity().getType().is(BLACKLISTED_SPAWNERS))
				return;
			int maxSpawned = 0;
			for (IdTagValue idTagValue : fixedSpawnerSpawnable) {
				if (idTagValue.id.matchesEntity(event.getEntity(), event.getEntity().level().dimension().location())) {
					maxSpawned = (int) idTagValue.value;
					break;
				}
			}
			if (maxSpawned <= 0) {
				double distance = Math.sqrt(spawnerPos.distSqr(level.getSharedSpawnPos()));
				maxSpawned = (int) ((minSpawnableMobs + (distance / 8d)) * spawnableMobsMultiplier);
			}

			if (spawnerCap.getSpawnedMobs() >= maxSpawned) {
				setSpawnerStatus(mobSpawner, true);
			}
			mobSpawner.setChanged();
		});
	}

	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickBlock event) {
		if (!event.getItemStack().is(SPAWNER_REACTIVATOR_TAG)
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

	@SubscribeEvent
	public void onSpawnCheck(MobSpawnEvent.SpawnPlacementCheck event) {
		if (!this.isEnabled()
				|| !ignoreLight
				|| event.getSpawnType() != MobSpawnType.SPAWNER
				|| event.getDefaultResult()
			/*|| !(event.getEntityType() instanceof EntityType<? extends Monster>)*/)
			return;

		//noinspection unchecked
		if (Monster.checkAnyLightMonsterSpawnRules((EntityType<? extends Monster>) event.getEntityType(), event.getLevel(), event.getSpawnType(), event.getPos(), event.getRandom()))
			event.setResult(Event.Result.ALLOW);
	}

	@SubscribeEvent
	public void onSpawnCheck(MobSpawnEvent.PositionCheck event) {
		if (!this.isEnabled()
				|| !ignoreLight
				|| event.getSpawnType() != MobSpawnType.SPAWNER)
			return;

		if (event.getEntity().checkSpawnObstruction(event.getLevel()))
			event.setResult(Event.Result.ALLOW);
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
			Object msg = new SpawnerStatusSync(spawner.getBlockPos(), disabled);
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
				|| !event.getItemStack().is(SPAWNER_REACTIVATOR_TAG))
			return;

		event.getToolTip().add(Component.translatable(SPAWNER_REACTIVATOR).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}