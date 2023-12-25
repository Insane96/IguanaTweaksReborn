package insane96mcp.survivalreimagined.module.items.copper;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.event.HurtItemStackEvent;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.generator.SRDamageTypeTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.ElectrocutionParticleMessage;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import insane96mcp.survivalreimagined.utils.MCUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Copper Tools Expansion", description = "Two new set of tools")
@LoadFeature(module = Modules.Ids.ITEMS)
public class CopperToolsExpansion extends Feature {
	public static final TagKey<Item> COPPER_TOOLS_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/hand/tools/copper"));
	public static final TagKey<Item> COATED_EQUIPMENT = TagKey.create(Registries.ITEM, new ResourceLocation(SurvivalReimagined.MOD_ID, "equipment/coated_copper"));

	public static final ILItemTier COPPER_ITEM_TIER = new ILItemTier(1, 65, 8f, 1.0f, 9, () -> Ingredient.of(Items.COPPER_INGOT));

	public static final RegistryObject<Item> COPPER_SWORD = SRRegistries.ITEMS.register("copper_sword", () -> new SwordItem(COPPER_ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> COPPER_SHOVEL = SRRegistries.ITEMS.register("copper_shovel", () -> new ShovelItem(COPPER_ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> COPPER_PICKAXE = SRRegistries.ITEMS.register("copper_pickaxe", () -> new PickaxeItem(COPPER_ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> COPPER_AXE = SRRegistries.ITEMS.register("copper_axe", () -> new AxeItem(COPPER_ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> COPPER_HOE = SRRegistries.ITEMS.register("copper_hoe", () -> new HoeItem(COPPER_ITEM_TIER, -1, -2.0F, new Item.Properties()));
	public static final SPShieldMaterial COPPER_SHIELD_MATERIAL = new SPShieldMaterial("copper", 134, () -> Items.COPPER_INGOT, 10, Rarity.COMMON);
	public static final RegistryObject<SPShieldItem> COPPER_SHIELD = SRRegistries.registerShield("copper_shield", COPPER_SHIELD_MATERIAL);

	public static final ILItemTier COATED_ITEM_TIER = new ILItemTier(3, 170, 7f, 1.5f, 5, () -> Ingredient.of(Items.OBSIDIAN));
	public static final RegistryObject<Item> COATED_SWORD = SRRegistries.ITEMS.register("coated_copper_sword", () -> new SwordItem(COATED_ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> COATED_SHOVEL = SRRegistries.ITEMS.register("coated_copper_shovel", () -> new ShovelItem(COATED_ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> COATED_PICKAXE = SRRegistries.ITEMS.register("coated_copper_pickaxe", () -> new PickaxeItem(COATED_ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> COATED_AXE = SRRegistries.ITEMS.register("coated_copper_axe", () -> new AxeItem(COATED_ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> COATED_HOE = SRRegistries.ITEMS.register("coated_copper_hoe", () -> new HoeItem(COATED_ITEM_TIER, -1, -2.0F, new Item.Properties()));

	public static final SPShieldMaterial COATED_SHIELD_MATERIAL = new SPShieldMaterial("coated_copper", 184, () -> Items.OBSIDIAN, 5, Rarity.COMMON);

	public static final RegistryObject<SPShieldItem> COATED_SHIELD = SRRegistries.registerShield("coated_copper_shield", COATED_SHIELD_MATERIAL);
    public static final RegistryObject<SimpleParticleType> ELECTROCUTION_SPARKS = SRRegistries.PARTICLE_TYPES.register("electrocution_sparks", () -> new SimpleParticleType(true));
	public static final RegistryObject<SoundEvent> ELECTROCUTION = SRRegistries.SOUND_EVENTS.register("electrocution", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(SurvivalReimagined.MOD_ID, "electrocution")));
	public static final TagKey<DamageType> DOESNT_TRIGGER_ELECTROCUTION = SRDamageTypeTagsProvider.create("doesnt_trigger_electrocution");

	public CopperToolsExpansion(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static final String COATED_TIMES_HIT = SurvivalReimagined.RESOURCE_PREFIX + "coated_times_hit";
	public static ResourceKey<DamageType> ELECTROCUTION_ATTACK = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(SurvivalReimagined.MOD_ID, "electrocution_attack"));

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| !event.getEntity().getMainHandItem().is(COPPER_TOOLS_EQUIPMENT)
				|| !event.getEntity().getMainHandItem().isCorrectToolForDrops(event.getState()))
			return;

		int y = event.getEntity().getBlockY();
		if (y > 64)
			return;
		//event.setNewSpeed(event.getNewSpeed() + EnchantmentsFeature.applyMiningSpeedModifiers((64 - y) * 0.1f, false, event.getEntity()));
		event.setNewSpeed(event.getNewSpeed() * (1f + (64 - y) * 0.01f));
	}

	@SubscribeEvent
	public void onHurtItemStack(HurtItemStackEvent event) {
		if (!this.isEnabled()
				|| !event.getStack().is(COPPER_TOOLS_EQUIPMENT)
				|| event.getPlayer() == null)
			return;

		int amount = event.getAmount();
		int newAmount = 0;
		int y = event.getEntity().getBlockY();
		if (y > 64)
			return;
		for (int i = 0; i < amount; i++) {
			//5% "more durability" per block below sea level (+420% at y=0 and +~700% at y=-54)
			if (event.getRandom().nextFloat() >= 1 - 1 / (1 + (64 - y) * 0.05))
				++newAmount;
		}
		event.setAmount(newAmount);
	}

	@SubscribeEvent
	public void onAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| event.getSource().is(DOESNT_TRIGGER_ELECTROCUTION)
				|| !(event.getSource().getEntity() instanceof Player player)
				|| !(event.getSource().getDirectEntity() instanceof Player)
				|| player.getAttackStrengthScale(1f) < 0.9f
				|| !(player.getMainHandItem().getItem() instanceof TieredItem tieredItem)
				|| tieredItem.getTier() != COATED_ITEM_TIER)
			return;

		CompoundTag tag = player.getMainHandItem().getOrCreateTag();
		if (!tag.contains(COATED_TIMES_HIT))
			tag.putInt(COATED_TIMES_HIT, 0);

		int hits = tag.getInt(COATED_TIMES_HIT);
		if (++hits >= 4) {
			hits = 0;
			electrocute(player, event.getEntity(), (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE));
		}
		tag.putInt(COATED_TIMES_HIT, hits);
	}

	@SubscribeEvent
	public void onParry(ShieldBlockEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Player player)
				|| !(event.getDamageSource().getDirectEntity() instanceof LivingEntity attacker)
				|| !(player.getUseItem().getItem() instanceof SPShieldItem spShieldItem)
				|| !player.getUseItem().is(COATED_SHIELD.get()))
			return;

		CompoundTag tag = player.getUseItem().getOrCreateTag();
		if (!tag.contains(COATED_TIMES_HIT))
			tag.putInt(COATED_TIMES_HIT, 0);

		int hits = tag.getInt(COATED_TIMES_HIT);
		if (++hits >= 4) {
			hits = 0;
			electrocute(player, attacker, (float) spShieldItem.getBlockedDamage(player.getUseItem(), attacker, player.level()));
		}
		tag.putInt(COATED_TIMES_HIT, hits);
	}

	private void electrocute(Player electrocuter, LivingEntity attacked, float damage) {
		DamageSource damageSource = electrocuter.damageSources().source(ELECTROCUTION_ATTACK, electrocuter);
		double range = 4.5d;
		ItemStack useItem = electrocuter.getUseItem();
		int hitEntities = 0;
		IntList listIdsOfHitEntities = new IntArrayList();
		List<LivingEntity> listOfHitEntities = new ArrayList<>();
		//Add the player to the list, so it doesn't get targeted
		listOfHitEntities.add(electrocuter);
		Entity lastEntityHit = attacked;
		do {
			List<LivingEntity> entitiesOfClass = electrocuter.level().getEntitiesOfClass(LivingEntity.class, lastEntityHit.getBoundingBox().inflate(range),
					livingEntity -> (electrocuter.canAttack(livingEntity)
									|| (livingEntity instanceof Player && electrocuter.canHarmPlayer((Player) livingEntity))) && !livingEntity.isDeadOrDying());
			LivingEntity target = MCUtils.getNearestEntity(entitiesOfClass, listOfHitEntities, attacked.position());
			if (target == null)
				break;
			listOfHitEntities.add(target);
			MCUtils.attackEntityIgnoreInvFrames(damageSource, damage, target, target, true);
			listIdsOfHitEntities.add(target.getId());
			target.playSound(ELECTROCUTION.get(), 0.4f, 1.0f);
			lastEntityHit = target;
			hitEntities++;
		} while (hitEntities < 4);

		Object msg = new ElectrocutionParticleMessage(listIdsOfHitEntities);
		for (Player levelPlayer : electrocuter.level().players()) {
			NetworkHandler.CHANNEL.sendTo(msg, ((ServerPlayer) levelPlayer).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !event.getItemStack().is(COATED_EQUIPMENT)
				|| event.getEntity() == null)
			return;

		int hits = event.getItemStack().getOrCreateTag().getInt(COATED_TIMES_HIT);
		//event.getToolTip().add(Component.translatable("survivalreimagined.electrocution.damage", event.getEntity().getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5f).withStyle(ChatFormatting.DARK_GREEN));
		event.getToolTip().add(Component.translatable("survivalreimagined.electrocution.charge", Math.round(hits / 3f * 100f)).withStyle(ChatFormatting.DARK_GRAY));
	}
}