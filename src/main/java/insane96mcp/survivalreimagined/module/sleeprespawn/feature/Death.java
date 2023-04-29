package insane96mcp.survivalreimagined.module.sleeprespawn.feature;

import insane96mcp.insanelib.ai.ILNearestAttackableTargetGoal;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.feature.GlobalExperience;
import insane96mcp.survivalreimagined.module.experience.feature.PlayerExperience;
import insane96mcp.survivalreimagined.module.sleeprespawn.block.GraveBlock;
import insane96mcp.survivalreimagined.module.sleeprespawn.block.GraveBlockEntity;
import insane96mcp.survivalreimagined.setup.SRBlockEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Label(name = "Death", description = "Changes to death")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Death extends Feature {

	public static final BlockWithItem GRAVE = BlockWithItem.register("grave", () -> new GraveBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).strength(1.5F, 6.0F)));
	public static final RegistryObject<BlockEntityType<?>> GRAVE_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("grave", () -> BlockEntityType.Builder.of(GraveBlockEntity::new, GRAVE.block().get()).build(null));

	public static final String PLAYER_GHOST = SurvivalReimagined.RESOURCE_PREFIX + "player_ghost";
	public static final String PLAYER_GHOST_LANG = SurvivalReimagined.MOD_ID + ".player_ghost";
	public static final String ITEMS_TO_DROP = SurvivalReimagined.RESOURCE_PREFIX + "items_to_drop";
	public static final String XP_TO_DROP = SurvivalReimagined.RESOURCE_PREFIX + "xp_to_drop";
	public static final String LAST_GHOST_UUID = SurvivalReimagined.RESOURCE_PREFIX + "last_ghost_uuid";
	public static final String GHOST_OWNER_UUID = SurvivalReimagined.RESOURCE_PREFIX + "ghost_owner_uuid";

	public static final UUID MOVEMENT_SPEED_BONUS = UUID.fromString("1905c271-160b-4560-9b76-c97b007657a5");
	public static final UUID ATTACK_DAMAGE_BONUS = UUID.fromString("bce0ee20-1358-4c8c-89ee-9446548a284b");
	public static final UUID ATTACK_DAMAGE_XP_BONUS = UUID.fromString("4b0d7d72-30cb-4200-9cc7-0944308b8bae");
	public static final UUID HEALTH_XP_BONUS = UUID.fromString("db05e364-0189-47bb-a6cb-487791c8dcd2");

	public Death(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof ServerPlayer player)
				|| player.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
				|| player.level.isOutsideBuildHeight(player.blockPosition().getY())
				|| (player.getInventory().isEmpty() && player.experienceLevel == 0))
			return;

		player.getLevel().setBlock(player.blockPosition(), GRAVE.block().get().defaultBlockState(), 3);
		if (player.getLevel().getBlockState(player.blockPosition().below()).canBeReplaced())
			player.getLevel().setBlock(player.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		GraveBlockEntity graveBlockEntity = (GraveBlockEntity) player.getLevel().getBlockEntity(player.blockPosition());
		List<ItemStack> items = new ArrayList<>();
		player.getInventory().items.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		player.getInventory().armor.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		player.getInventory().offhand.forEach(itemStack -> {
			if (!itemStack.isEmpty())
				items.add(itemStack);
		});
		graveBlockEntity.setItems(items);
		int xpDropped = PlayerExperience.getExperienceOnDeath(player, true);
		graveBlockEntity.setXpStored(xpDropped);
		player.setExperienceLevels(0);
		player.setExperiencePoints(0);
		player.getInventory().clearContent();
		/*Zombie zombie = EntityType.ZOMBIE.create(player.getLevel());
		if (zombie == null)
			return;
		zombie.setPos(player.position());
		zombie.setPersistenceRequired();
		zombie.lootTable = new ResourceLocation("minecraft:empty");
		zombie.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, 0d);
		zombie.getPersistentData().putBoolean(PLAYER_GHOST, true);
		zombie.getPersistentData().putBoolean(EAStrings.Tags.Zombie.MINER, true);
		zombie.getPersistentData().putBoolean("mobspropertiesrandomness:processed", true);
		zombie.setCustomName(Component.translatable(PLAYER_GHOST_LANG, player.getName().getString()));
		if (zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null)
			zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1d);
		MCUtils.applyModifier(zombie, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_BONUS, "Ghost movement speed bonus", 0.5d, AttributeModifier.Operation.MULTIPLY_BASE, true);
		MCUtils.applyModifier(zombie, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_BONUS, "Ghost attack damage bonus", 1d, AttributeModifier.Operation.ADDITION, true);
		zombie.setSilent(true);
		zombie.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, false, false));
		zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
		ListTag listTag = new ListTag();
		for (ItemStack item : player.getInventory().items) {
			listTag.add(item.save(new CompoundTag()));
		}
		for (ItemStack item : player.getInventory().armor) {
			listTag.add(item.save(new CompoundTag()));
		}
		for (ItemStack item : player.getInventory().offhand) {
			listTag.add(item.save(new CompoundTag()));
		}
		zombie.getPersistentData().put(ITEMS_TO_DROP, listTag);
		int xpDropped = PlayerExperience.getExperienceOnDeath(player, true);
		zombie.getPersistentData().putInt(XP_TO_DROP, xpDropped);
		MCUtils.applyModifier(zombie, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_XP_BONUS, "Ghost attack damage bonus by XP", xpDropped * 0.001d, AttributeModifier.Operation.MULTIPLY_BASE, true);
		MCUtils.applyModifier(zombie, Attributes.MAX_HEALTH, HEALTH_XP_BONUS, "Ghost health bonus by XP", xpDropped * 0.001d, AttributeModifier.Operation.MULTIPLY_BASE, true);
		zombie.getPersistentData().putUUID(GHOST_OWNER_UUID, player.getUUID());
		player.setExperienceLevels(0);
		player.setExperiencePoints(0);
		player.level.addFreshEntity(zombie);
		player.getInventory().clearContent();
		player.getPersistentData().putUUID(LAST_GHOST_UUID, zombie.getUUID());*/
	}

	//Remove targeting goal. Only attack players that attack the ghost
	@SubscribeEvent
	public void onGhostJoinWorld(EntityJoinLevelEvent event) {
		if (!event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			return;

		Zombie zombie = (Zombie) event.getEntity();
		zombie.targetSelector.getAvailableGoals().removeIf(wrappedGoal -> wrappedGoal.getGoal() instanceof NearestAttackableTargetGoal<?> || wrappedGoal.getGoal() instanceof ILNearestAttackableTargetGoal<?>);
		zombie.goalSelector.addGoal(0, new FloatGoal(zombie));
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity().level.isClientSide
				|| !event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			return;

		/*ListTag listTag = event.getEntity().getPersistentData().getList(ITEMS_TO_DROP, Tag.TAG_COMPOUND);
		for (Tag tag : listTag) {
			if (tag instanceof CompoundTag compoundTag) {
				ItemStack stack = ItemStack.of(compoundTag);
				ItemEntity itemEntity = new ItemEntity(event.getEntity().getLevel(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack);
				event.getEntity().level.addFreshEntity(itemEntity);
			}
		}*/
		int experienceToDrop = event.getEntity().getPersistentData().getInt(XP_TO_DROP);
		if (experienceToDrop > 0) {
			ExperienceOrb xpOrb = new ExperienceOrb(event.getEntity().level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), experienceToDrop);
			xpOrb.getPersistentData().putBoolean(GlobalExperience.XP_PROCESSED, true);
			event.getEntity().level.addFreshEntity(xpOrb);
		}
	}

	/*@SubscribeEvent
	public void onGhostRightClick(PlayerInteractEvent.EntityInteract event) {
		if (!event.getTarget().getPersistentData().contains(PLAYER_GHOST)
			|| event.getEntity().level.isClientSide)
			return;

		ListTag listTag = event.getTarget().getPersistentData().getList(ITEMS_TO_DROP, Tag.TAG_COMPOUND);
		for (Tag tag : listTag) {
			if (tag instanceof CompoundTag compoundTag) {
				ItemStack stack = ItemStack.of(compoundTag);
				ItemEntity itemEntity = new ItemEntity(event.getTarget().getLevel(), event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), stack);
				event.getTarget().level.addFreshEntity(itemEntity);
			}
		}
		event.getTarget().getPersistentData().remove(ITEMS_TO_DROP);
		InteractWithGhostTrigger.TRIGGER.trigger((ServerPlayer) event.getEntity());
		if (event.getTarget().getPersistentData().getInt(XP_TO_DROP) <= 0)
			event.getTarget().kill();
	}*/

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| event.getEntity().tickCount % 20 != 2
				|| event.getEntity().level.isClientSide
				|| !event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			return;

		Zombie zombie = (Zombie) event.getEntity();
		if (zombie.getPersistentData().contains(GHOST_OWNER_UUID)) {
			UUID owner = zombie.getPersistentData().getUUID(GHOST_OWNER_UUID);
			Optional<Player> oPlayer = getPlayerOwner((ServerLevel) zombie.level, owner);
			oPlayer.ifPresent(player -> {
				if (player.getPersistentData().contains(LAST_GHOST_UUID)) {
					UUID lastGhost = player.getPersistentData().getUUID(LAST_GHOST_UUID);
					if (!lastGhost.equals(zombie.getUUID()))
						zombie.kill();
				}
			});
		}
		if (event.getEntity().level.hasNearbyAlivePlayer(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), 80))
			event.getEntity().setGlowingTag(true);
		event.getEntity().setTicksFrozen(0);
	}

	private Optional<Player> getPlayerOwner(ServerLevel level, UUID playerUUID) {
		for(Player player : level.players()) {
			UUID uuid = player.getUUID();
			if(uuid.equals(playerUUID))
				return Optional.of(player);
		}

		return Optional.empty();
	}

	@SubscribeEvent
	public void canConvert(LivingConversionEvent.Pre event) {
		if (event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			event.setCanceled(true);
	}
}