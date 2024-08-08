package insane96mcp.iguanatweaksreborn.module.sleeprespawn.death;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRDamageTypeTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.experience.PlayerExperience;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.death.integration.ToolBelt;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.insanelib.world.scheduled.ScheduledTasks;
import insane96mcp.insanelib.world.scheduled.ScheduledTickTask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Death", description = "Changes to death")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Death extends Feature {

	public static final SimpleBlockWithItem GRAVE = SimpleBlockWithItem.register("grave", () -> new GraveBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).forceSolidOn().strength(1.5F, 6.0F)));
	public static final RegistryObject<BlockEntityType<?>> GRAVE_BLOCK_ENTITY_TYPE = ITRRegistries.BLOCK_ENTITY_TYPES.register("grave", () -> BlockEntityType.Builder.of(GraveBlockEntity::new, GRAVE.block().get()).build(null));
	public static final GameRules.Key<GameRules.BooleanValue> RULE_DEATHGRAVE = GameRules.register("iguanatweaks:deathGrave", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
	public static final GameRules.Key<GameRules.IntegerValue> RULE_DEATHLOSEITEMSPERCENTAGE = GameRules.register("iguanatweaks:deathLoseItemsPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(0));
	public static final TagKey<DamageType> DOESNT_SPAWN_GRAVE = ITRDamageTypeTagsProvider.create("doesnt_spawn_grave");

	public static final String KILLED_PLAYER = IguanaTweaksReborn.RESOURCE_PREFIX + "killed_player";
	public static final String PLAYER_KILLER_LANG = IguanaTweaksReborn.MOD_ID + ".player_killer";
	public static final TagKey<EntityType<?>> KILLER_BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "killer_blacklist"));

	@Config
	@Label(name = "Player's killer bounty", description = "If true, the player's killer will not despawn and when killed will drop 4x more items and experience.")
	public static Boolean vindicationVsKiller = true;
	@Config
	@Label(name = "Grave keeps experience", description = "If true, the player's experience is stored in the grave.")
	public static Boolean graveExperience = false;

	public Death(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEarly(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof ServerPlayer player))
			return;
		int lostItemsPercentage = player.level().getGameRules().getInt(RULE_DEATHLOSEITEMSPERCENTAGE);
		if (lostItemsPercentage == 0)
			return;
		if (lostItemsPercentage == 100)
			player.getInventory().clearContent();
		else {
			tryLoseItems(player.getInventory(), player.getInventory().items, player.getRandom(), lostItemsPercentage);
			tryLoseItems(player.getInventory(), player.getInventory().armor, player.getRandom(), lostItemsPercentage);
			tryLoseItems(player.getInventory(), player.getInventory().offhand, player.getRandom(), lostItemsPercentage);
		}
	}

	private static void tryLoseItems(Inventory inventory, List<ItemStack> items, RandomSource random, int chance) {
		items.forEach(item -> {
			if (random.nextInt(100) < chance)
				inventory.removeItem(item);
		});
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof ServerPlayer player)
				|| player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
				|| !player.level().getGameRules().getBoolean(RULE_DEATHGRAVE)
				|| player.level().isOutsideBuildHeight(player.blockPosition().getY()))
			return;

		breakOldGrave(player);
		summonGrave(player, event.getSource());
	}

	public static void breakOldGrave(ServerPlayer player) {
		if (player.getLastDeathLocation().isEmpty())
			return;

		GlobalPos pos = player.getLastDeathLocation().get();
		if (player.getServer() == null)
			return;
		ServerLevel level = player.getServer().getLevel(pos.dimension());
		if (level == null)
			return;
		if (level.isLoaded(pos.pos()) && level.getBlockState(pos.pos()).is(GRAVE.block().get())) {
			GraveBlock.dropGraveItems(level, pos.pos());
			//level.destroyBlock(pos.pos(), true, player);

			int chunkX = SectionPos.blockToSectionCoord(pos.pos().getX());
			int chunkZ = SectionPos.blockToSectionCoord(pos.pos().getZ());
			level.setChunkForced(chunkX, chunkZ, false);
		}
	}

	public static void summonGrave(ServerPlayer player, DamageSource source) {
		if (source.is(DOESNT_SPAWN_GRAVE))
			return;
		BlockPos pos = player.blockPosition();
		if (pos.getY() < player.level().getMinBuildHeight())
			pos = pos.atY(player.level().getMinBuildHeight() + 1);
		if (player.level().getBlockState(pos.below()).canBeReplaced())
			player.level().setBlock(pos.below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		BlockState grave = GRAVE.block().get().defaultBlockState();
		if (player.level().getFluidState(pos).getType() == Fluids.WATER)
			grave = grave.setValue(GraveBlock.WATERLOGGED, true);
		player.level().destroyBlock(pos, true, player);
		player.level().setBlock(pos, grave, 3);
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) player.level().getBlockEntity(pos);
		if (graveBlockEntity == null)
			return;
		List<ItemStack> items = new ArrayList<>();
		player.getInventory().items.forEach(itemStack -> {
			if (!itemStack.isEmpty() && itemStack.getEnchantmentLevel(Enchantments.VANISHING_CURSE) == 0)
				items.add(itemStack);
		});
		player.getInventory().armor.forEach(itemStack -> {
			if (!itemStack.isEmpty() && itemStack.getEnchantmentLevel(Enchantments.VANISHING_CURSE) == 0)
				items.add(itemStack);
		});
		player.getInventory().offhand.forEach(itemStack -> {
			if (!itemStack.isEmpty() && itemStack.getEnchantmentLevel(Enchantments.VANISHING_CURSE) == 0)
				items.add(itemStack);
		});
		if (ModList.get().isLoaded("toolbelt"))
			ToolBelt.onDeath(items, player);
		if (items.isEmpty() && (player.experienceLevel <= 0 || !graveExperience))
			return;
		graveBlockEntity.setItems(items);
		if (graveExperience && player.experienceLevel > 0) {
			int xpDropped = PlayerExperience.getExperienceOnDeath(player, true);
			graveBlockEntity.setXpStored(xpDropped);
			player.setExperienceLevels(0);
			player.setExperiencePoints(0);
		}
		graveBlockEntity.setOwner(player.getUUID());
		graveBlockEntity.setDeathNumber(player.getStats().getValue(Stats.CUSTOM.get(Stats.DEATHS)) + 1);
		player.getInventory().clearContent();
		graveBlockEntity.setMessage(player.getCombatTracker().getDeathMessage());

		int chunkX = SectionPos.blockToSectionCoord(pos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
		((ServerLevel)player.level()).setChunkForced(chunkX, chunkZ, true);

		if (vindicationVsKiller && source.getEntity() instanceof Mob killer && !killer.getPersistentData().contains(KILLED_PLAYER)) {
			if (killer.isRemoved() || killer.isDeadOrDying() || killer.getType().is(KILLER_BLACKLIST))
				return;
			ScheduledTasks.schedule(new ScheduledTickTask(1) {
				@Override
				public void run() {
					killer.setPersistenceRequired();
					double experienceMultiplier = 5d;
					if (killer.getPersistentData().contains(ILStrings.Tags.EXPERIENCE_MULTIPLIER))
						experienceMultiplier *= killer.getPersistentData().getDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER);
					killer.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, experienceMultiplier);
					killer.getPersistentData().putUUID(KILLED_PLAYER, player.getUUID());
					killer.setCustomName(Component.translatable(PLAYER_KILLER_LANG, player.getGameProfile().getName()));
				}
			});
		}
	}

	@SubscribeEvent
	public void onPlayerKillerDeath(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Mob mob)
				|| !mob.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
				|| !mob.getPersistentData().contains(KILLED_PLAYER)
				|| mob.level().isClientSide)
			return;
		Component deathMessage = event.getEntity().getCombatTracker().getDeathMessage();

		//noinspection DataFlowIssue
		mob.level().getServer().getPlayerList().broadcastSystemMessage(deathMessage, false);
	}
}