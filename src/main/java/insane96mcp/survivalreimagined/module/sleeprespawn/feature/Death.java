package insane96mcp.survivalreimagined.module.sleeprespawn.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Death", description = "Changes to death")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Death extends Feature {

	public static final String PLAYER_GHOST = SurvivalReimagined.RESOURCE_PREFIX + "player_ghost";
	public static final String PLAYER_GHOST_LANG = SurvivalReimagined.MOD_ID + ".player_ghost";
	public static final String ITEMS_TO_DROP = SurvivalReimagined.RESOURCE_PREFIX + "items_to_drop";
	//public static final String XP_TO_DROP = SurvivalReimagined.RESOURCE_PREFIX + "xp_to_drop";

	public Death(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof ServerPlayer player)
				|| player.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
				|| player.getInventory().isEmpty())
			return;

		Zombie zombie = EntityType.ZOMBIE.create(player.getLevel());
		if (zombie == null)
			return;
		zombie.setPos(player.position());
		zombie.setPersistenceRequired();
		zombie.lootTable = new ResourceLocation("minecraft:empty");
		zombie.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, 0d);
		zombie.getPersistentData().putBoolean(PLAYER_GHOST, true);
		zombie.setCustomName(Component.translatable(PLAYER_GHOST_LANG, player.getName().getString()));
		if (zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null)
			zombie.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1d);
		zombie.setSilent(true);
		zombie.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, false, false));
		zombie.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
		for (int i = 0; i < 4; i++) {
			EquipmentSlot slot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, i);
			zombie.setItemSlot(slot, player.getInventory().getArmor(i));
			zombie.setGuaranteedDrop(slot);
			player.setItemSlot(slot, ItemStack.EMPTY);
		}
		zombie.setItemSlot(EquipmentSlot.MAINHAND, player.getMainHandItem());
		zombie.setGuaranteedDrop(EquipmentSlot.MAINHAND);
		player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		zombie.setItemSlot(EquipmentSlot.OFFHAND, player.getOffhandItem());
		zombie.setGuaranteedDrop(EquipmentSlot.OFFHAND);
		player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		ListTag listTag = new ListTag();
		for (ItemStack item : player.getInventory().items) {
			listTag.add(item.save(new CompoundTag()));
		}
		zombie.getPersistentData().put(ITEMS_TO_DROP, listTag);
		player.level.addFreshEntity(zombie);
		player.getInventory().clearContent();
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity().level.isClientSide
				|| !event.getEntity().getPersistentData().contains(ITEMS_TO_DROP))
			return;

		ListTag listTag = event.getEntity().getPersistentData().getList(ITEMS_TO_DROP, Tag.TAG_COMPOUND);
		for (Tag tag : listTag) {
			if (tag instanceof CompoundTag compoundTag) {
				ItemStack stack = ItemStack.of(compoundTag);
				ItemEntity itemEntity = new ItemEntity(event.getEntity().getLevel(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack);
				event.getEntity().level.addFreshEntity(itemEntity);
			}
		}
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| event.getEntity().tickCount % 20 != 2
				|| !event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			return;

		if (event.getEntity().level.hasNearbyAlivePlayer(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), 64))
			event.getEntity().setGlowingTag(true);
	}

	@SubscribeEvent
	public void canConvert(LivingConversionEvent.Pre event) {
		if (event.getEntity().getPersistentData().contains(PLAYER_GHOST))
			event.setCanceled(true);
	}
}