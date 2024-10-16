package insane96mcp.iguanatweaksreborn.module.experience.enchantments;

import com.google.common.collect.Lists;
import com.teamabnormals.allurement.core.AllurementConfig;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.FireAspect;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.IAttributeEnchantment;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Knockback;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Luck;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BaneOfSSSS;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.Sharpness;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.Smite;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection.*;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.integration.Allurement;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatistics;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatsReloadListener;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.*;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Label(name = "Enchantments", description = "Changes to some enchantments related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends JsonFeature {
	public static final RegistryObject<Enchantment> SHARPNESS = ITRRegistries.ENCHANTMENTS.register("sharpness", Sharpness::new);
	public static final RegistryObject<Enchantment> SMITE = ITRRegistries.ENCHANTMENTS.register("smite", Smite::new);
	public static final RegistryObject<Enchantment> BANE_OF_SSSSS = ITRRegistries.ENCHANTMENTS.register("bane_of_sssss", BaneOfSSSS::new);
	public static final RegistryObject<Enchantment> FIRE_ASPECT = ITRRegistries.ENCHANTMENTS.register("fire_aspect", FireAspect::new);
	public static final RegistryObject<Enchantment> KNOCKBACK = ITRRegistries.ENCHANTMENTS.register("knockback", Knockback::new);
	public static final RegistryObject<Enchantment> LUCK = ITRRegistries.ENCHANTMENTS.register("luck", Luck::new);
	public static final RegistryObject<Enchantment> PROTECTION = ITRRegistries.ENCHANTMENTS.register("protection", OverallProtection::new);
	public static final RegistryObject<Enchantment> BLAST_PROTECTION = ITRRegistries.ENCHANTMENTS.register("blast_protection", BlastProtection::new);
	public static final RegistryObject<Enchantment> FIRE_PROTECTION = ITRRegistries.ENCHANTMENTS.register("fire_protection", FireProtection::new);
	public static final RegistryObject<Enchantment> PROJECTILE_PROTECTION = ITRRegistries.ENCHANTMENTS.register("projectile_protection", ProjectileProtection::new);
	public static final RegistryObject<Enchantment> FEATHER_FALLING = ITRRegistries.ENCHANTMENTS.register("feather_falling", FeatherFalling::new);
	@Config
	@Label(name = "Infinity overhaul", description = "Infinity can go up to level 4. Each level makes an arrow have only 1 in level+1 chance to consume. E.g. with Infinity 4 there's 1 in 5 chance to consume the arrow, and 4 in 5 to not consume it.")
	public static Boolean infinityOverhaul = true;
	@Config
	@Label(name = "Less unbreakable unbreaking", description = "Unbreaking max level is now 5 and tool lasts 50% more per level")
	public static Boolean unbreakingOverhaul = true;
	@Config
	@Label(name = "Small Thorns Overhaul", description = "Thorns is no longer compatible with other protections, but deals damage every time (higher levels deal more damage) and no longer damages items.")
	public static Boolean thornsOverhaul = true;
	@Config
	@Label(name = "Mending Nerf", description = "Mending only makes the tool repair by one durability every 2 xp instead of 2 durability/1 xp.")
	public static Boolean mendingNerf = true;
	@Config
	@Label(name = "Respiration Nerf", description = "Respiration decreases air consumption by 50% per level instead of 100%.")
	public static Boolean respirationNerf = true;

	@Config
	@Label(name = "Better Efficiency Formula", description = "Change the bonus efficiency formula from `lvl*lvl+1` to `tool_efficiency * (0.5*lvl)`")
	public static Boolean changeEfficiencyFormula = true;

	@Config
	@Label(name = "Nerf fortune", description = "The ore_drops formula is changed to 20%/50%/85%/125% drop increase from 33%/75%/120%/166%")
	public static Boolean nerfFortune = true;

	@Config(min = 0d, max = 2d)
	@Label(name = "Power Enchantment Damage", description = "Set arrow's damage increase with the Power enchantment (vanilla is 0.5). If set to a value != 0.5 the flat 0.5 bonus is also removed. Set to 0.5 to disable.")
	public static Double powerEnchantmentDamage = 0.2d;

	@Config
	@Label(name = "Power Enchantment Multiplier", description = "If true, the formula for bonus damage for arrows is changed from `'Power Enchantment Damage' + 'Power Enchantment Damage' * lvl` to `base_damage * 'Power Enchantment Damage' * lvl`.")
	public static Boolean powerEnchantmentMultiplier = true;

	@Config
	@Label(name = "Prevent farmland trampling with Feather Falling")
	public static Boolean preventFarmlandTramplingWithFeatherFalling = true;
	@Config
	@Label(name = "Replace protection enchantments", description = "If true, vanilla protection enchantments are replaced with mod's ones. To re-enable vanilla enchantments refer to `disabled_enchantments.json`.\n" +
			"Protection has only one level, protects 6% per level and is treasure. Other protections work the same except for projectile that reduces the sight range of mobs by 2% per level. Feather falling protects for 16% per level instead of 12% + 1 per level.")
	public static Boolean replaceProtectionEnchantments = true;
	@Config
	@Label(name = "Replace damaging enchantments", description = """
            If true, vanilla damaging enchantments (such as smite or sharpness) are replaced with mod's ones. To re-enable vanilla enchantments refer to `disabled_enchantments.json`.
            Changes to damaging enchantments:
            Enchantments deal bonus damage based off the item's attack damage. So Sharpness on a Sword adds less damage than Sharpness on an Axe.
            Sharpness deals +0.5 damage per level
            Smite deals +1 damage per level to undead and applies weakness
            Bane of Arthropods has been replaced with Bane of SSSSS that deals +1 damage per level to arthropods and creepers and applies slowness""")
	public static Boolean replaceDamagingEnchantments = true;
	@Config
	@Label(name = "Replace looting, fortune and LotS enchantments", description = "If true, vanilla looting, fortune and Luck of the Sea enchantments are replaced with a single one: Luck. To re-enable vanilla enchantments refer to `disabled_enchantments.json`.")
	public static Boolean replaceLuckEnchantments = true;

	@Config
	@Label(name = "Enchantments info", description = "If true and shift it pressed, items will show enchantment info below the enchantments")
	public static Boolean enchantmentsInfo = true;

	public static final ArrayList<IdTagMatcher> DISABLED_ENCHANTMENTS_DEFAULT = new ArrayList<>(List.of(
			IdTagMatcher.newId("minecraft:sharpness"),
			IdTagMatcher.newId("minecraft:smite"),
			IdTagMatcher.newId("minecraft:bane_of_arthropods"),
			IdTagMatcher.newId("minecraft:sharpness"),
			IdTagMatcher.newId("minecraft:fire_aspect"),
			IdTagMatcher.newId("minecraft:knockback"),
			IdTagMatcher.newId("minecraft:protection"),
			IdTagMatcher.newId("minecraft:blast_protection"),
			IdTagMatcher.newId("minecraft:projectile_protection"),
			IdTagMatcher.newId("minecraft:fire_protection"),
			IdTagMatcher.newId("minecraft:feather_falling"),
			IdTagMatcher.newId("minecraft:looting"),
			IdTagMatcher.newId("minecraft:fortune"),
			IdTagMatcher.newId("minecraft:luck_of_the_sea"),
			IdTagMatcher.newId("farmersdelight:backstabbing")

	));
	public static final ArrayList<IdTagMatcher> disabledEnchantments = new ArrayList<>();

	public EnchantmentsFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_enchantments"), new SyncType(json -> loadAndReadJson(json, disabledEnchantments, DISABLED_ENCHANTMENTS_DEFAULT, IdTagMatcher.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("disabled_enchantments.json", disabledEnchantments, DISABLED_ENCHANTMENTS_DEFAULT, IdTagMatcher.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_enchantments")));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (infinityOverhaul) {
			Enchantments.INFINITY_ARROWS.rarity = Enchantment.Rarity.RARE;
			if (ModList.get().isLoaded("allurement"))
				AllurementConfig.COMMON.infinityRequiresArrows.set(true);
		}
		else
			Enchantments.INFINITY_ARROWS.rarity = Enchantment.Rarity.VERY_RARE;

		if (thornsOverhaul)
			Enchantments.THORNS.rarity = Enchantment.Rarity.RARE;
		else
			Enchantments.THORNS.rarity = Enchantment.Rarity.VERY_RARE;

		//Can this make something blow up?
		if (replaceLuckEnchantments)
			Enchantments.BLOCK_FORTUNE = LUCK.get();
	}

	@SubscribeEvent
	public void onAttributeModifiers(ItemAttributeModifierEvent event) {
		event.getItemStack().getAllEnchantments().forEach((enchantment, lvl) -> {
			if (event.getItemStack().getItem() instanceof ArmorItem armorItem
					&& armorItem.getEquipmentSlot() != event.getSlotType())
				return;
			if (enchantment instanceof IAttributeEnchantment attributeEnchantment)
				attributeEnchantment.applyAttributeModifier(event, lvl);
		});
	}

	public static boolean isEnchantmentDisabled(Enchantment enchantment) {
		if (!Feature.isEnabled(EnchantmentsFeature.class))
			return false;

		for (IdTagMatcher idTagMatcher : disabledEnchantments) {
			if (idTagMatcher.matchesEnchantment(enchantment))
				return true;
		}
		return false;
	}

	public static boolean isUnbreakingOverhaul() {
		return Feature.isEnabled(EnchantmentsFeature.class) && unbreakingOverhaul;
	}

	public static float unbreakingBonus(int lvl) {
		if (isUnbreakingOverhaul()) {
			//Tools last 50% more per level
			return 1 - 1 / (1 + (lvl * 0.5f));
		}
		else {
			return 1f / (lvl + 1);
		}
	}

	public static boolean isBetterEfficiencyFormula() {
		return Feature.isEnabled(EnchantmentsFeature.class) && changeEfficiencyFormula;
	}

	public static float getEfficiencyBonus(float toolEfficiency, int lvl) {
		if (isBetterEfficiencyFormula()) {
			return toolEfficiency * lvl * 0.25f;
		}
		else {
			return lvl * lvl + 1;
		}
	}

	public static boolean isThornsOverhaul() {
		return Feature.isEnabled(EnchantmentsFeature.class) && thornsOverhaul;
	}

	//Run before knockback feature
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onKnockback(LivingKnockBackEvent event) {
		if (!this.isEnabled()
				|| event.getEntity().getCombatTracker().entries.isEmpty())
			return;

		Entity directAttacker = event.getEntity().getCombatTracker().entries.get(event.getEntity().getCombatTracker().entries.size() - 1).source().getDirectEntity();
		if (!(directAttacker instanceof LivingEntity attacker))
			return;

		float knockback = attacker.getMainHandItem().getEnchantmentLevel(KNOCKBACK.get());
		if (knockback == 0)
			return;
		if (attacker instanceof Player player) {
			float f = player.getAttackStrengthScale(0.5f);
			knockback *= f * f;
		}
		for (IdTagValue itemKnockbackMultiplier : insane96mcp.iguanatweaksreborn.module.combat.Knockback.knockbackMultipliers) {
			if (itemKnockbackMultiplier.id.matchesItem(attacker.getMainHandItem()))
				knockback *= (float) itemKnockbackMultiplier.value;
		};
		event.setStrength(event.getStrength() + knockback);
	}

	@SubscribeEvent
	public void onLivingHurt(LivingEvent.LivingVisibilityEvent event) {
		if (!this.isEnabled())
			return;

		int lvl = EnchantmentHelper.getEnchantmentLevel(PROJECTILE_PROTECTION.get(), event.getEntity());
		if (lvl < 1)
			return;

		event.modifyVisibility(1f - 0.15f * lvl);
	}

	public static float bonusDamageEnchantment(Enchantment enchantment, int lvl, LivingEntity attacker, Entity target) {
		if (!(enchantment instanceof BonusDamageEnchantment bonusDamageEnchantment))
			return 0f;
		float damageBonus = bonusDamageEnchantment.getDamageBonus(attacker, target, attacker.getMainHandItem(), lvl);
		if (attacker instanceof Player player) {
			float f = player.getAttackStrengthScale(0.5f);
			damageBonus *= f * f;
		}
		return damageBonus;
	}

	@SubscribeEvent
	public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (!this.isEnabled()
				|| !preventFarmlandTramplingWithFeatherFalling
				|| !(event.getEntity() instanceof LivingEntity entity)
				|| (EnchantmentHelper.getEnchantmentLevel(Enchantments.FALL_PROTECTION, entity) <= 0 && EnchantmentHelper.getEnchantmentLevel(FEATHER_FALLING.get(), entity) <= 0))
			return;

		event.setCanceled(true);
	}

	//Override vanilla behaviour
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onExperiencePickUp(PlayerXpEvent.PickupXp event) {
		if (!this.isEnabled()
				|| !mendingNerf)
			return;

		event.setCanceled(true);
		Player player = event.getEntity();
		player.takeXpDelay = 2;
		player.take(event.getOrb(), 1);
		if (ModList.get().isLoaded("allurement"))
			Allurement.onExperiencePickup(player, event.getOrb());
		int i = repairPlayerItems(event.getOrb(), player, event.getOrb().value);
		if (i > 0) {
			player.giveExperiencePoints(i);
		}

		--event.getOrb().count;
		if (event.getOrb().count == 0) {
			event.getOrb().discard();
		}
	}

	private static int repairPlayerItems(ExperienceOrb xpOrb, Player player, int xp) {
		ItemStack stack = getRandomItemWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        if (stack == null)
            return xp;

		float repairAmount = Math.min(xpOrb.value * 0.5f, stack.getDamageValue());
		stack.setDamageValue(stack.getDamageValue() - MathHelper.getAmountWithDecimalChance(player.getRandom(), repairAmount));
		int j = xp - Math.round(repairAmount * 2f);
		return j > 0 ? repairPlayerItems(xpOrb, player, j) : 0;
    }

	public static ItemStack getRandomItemWith(Enchantment enchantment, LivingEntity livingEntity, Predicate<ItemStack> pStackCondition) {
		Map<EquipmentSlot, ItemStack> map = enchantment.getSlotItems(livingEntity);
		if (map.isEmpty())
			return null;

		List<ItemStack> list = Lists.newArrayList();

		for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
			ItemStack itemStack = entry.getValue();
			if (!itemStack.isEmpty() && EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack) > 0 && pStackCondition.test(itemStack)) {
				list.add(itemStack);
			}
		}
		if (ModList.get().isLoaded("toolbelt")) {
			/*List<ItemStack> toolbeltStacks = new ArrayList<>();
			ToolBelt.putItems(toolbeltStacks, livingEntity);
			toolbeltStacks.removeIf(stack -> stack.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) <= 0 || !stack.isDamaged());
			list.addAll(toolbeltStacks);*/
		}

		return list.isEmpty() ? null : list.get(livingEntity.getRandom().nextInt(list.size()));
	}

	/**
	 * Make Allurement's reforming work with ToolBelt items
	 */
	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
		/*if (!ModList.get().isLoaded("toolbelt")
				|| !ModList.get().isLoaded("allurement")
				|| event.getEntity().level().getGameTime() % AllurementConfig.COMMON.reformingTickRate.get() != 0)
			return;
		LivingEntity entity = event.getEntity();
		Level level = entity.getCommandSenderWorld();
		List<ItemStack> stacksInToolbelt = new ArrayList<>();
		ToolBelt.putItems(stacksInToolbelt, entity);
		for (ItemStack stack : stacksInToolbelt) {
			int lvl = EnchantmentHelper.getTagEnchantmentLevel(AllurementEnchantments.REFORMING.get(), stack);
			if (!stack.isEmpty() && stack.isDamaged() && lvl > 0) {
				stack.setDamageValue(stack.getDamageValue() - 1);
			}
		}*/
	}

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		int lvl = event.getEntity().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(FEATHER_FALLING.get());
		if (lvl <= 0)
			return;
		event.setDistance(event.getDistance() - lvl);
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !enchantmentsInfo
				|| (!event.getItemStack().isEnchanted() && !event.getItemStack().is(Items.ENCHANTED_BOOK))
				|| !Screen.hasShiftDown())
			return;
		HashMap<Integer, Component> tooltipsToAdd = new HashMap<>();
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
		AtomicInteger added = new AtomicInteger();
		for (Component line : event.getToolTip()) {
			if (line.getContents() instanceof TranslatableContents translatableContents) {
				Optional<Enchantment> oEnchantment = enchantments.keySet().stream().filter(e -> translatableContents.getKey().equals(e.getDescriptionId())).findAny();
				oEnchantment.ifPresent(enchantment -> {
					tooltipsToAdd.put(event.getToolTip().indexOf(line) + 1 + added.getAndIncrement(), CommonComponents.space().append(Component.translatable(enchantment.getDescriptionId() + ".info").withStyle(ChatFormatting.LIGHT_PURPLE)));
				});
			}
		}
		for (Map.Entry<Integer, Component> tooltipToAdd : tooltipsToAdd.entrySet()) {
			event.getToolTip().add(tooltipToAdd.getKey(), tooltipToAdd.getValue());
		}
	}

	public static boolean isInfinityOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && infinityOverhaul;
	}

	public static boolean shouldReplaceBaneOfArthropods(Enchantment enchantment) {
		return enchantment ==  Enchantments.BANE_OF_ARTHROPODS
				&& Feature.isEnabled(EnchantmentsFeature.class)
				&& EnchantmentsFeature.isEnchantmentDisabled(Enchantments.BANE_OF_ARTHROPODS)
				&& !EnchantmentsFeature.isEnchantmentDisabled(EnchantmentsFeature.BANE_OF_SSSSS.get());
	}

	public static boolean shouldReplaceWithLuck(Enchantment enchantment) {
		return (enchantment == Enchantments.BLOCK_FORTUNE || enchantment == Enchantments.MOB_LOOTING || enchantment == Enchantments.FISHING_LUCK)
				&& Feature.isEnabled(EnchantmentsFeature.class)
				&& EnchantmentsFeature.replaceLuckEnchantments;
	}

	public static int getEnchantmentValue(ItemStack stack) {
		for (ItemStatistics itemStatistics : ItemStatsReloadListener.Stats) {
			if (itemStatistics.enchantability() != null && itemStatistics.item().matchesItem(stack))
				return itemStatistics.enchantability();
		}
		return stack.getEnchantmentValue();
	}

	public static float applyMiningSpeedModifiers(float miningSpeed, boolean applyEfficiency, LivingEntity entity) {
		if (applyEfficiency) {
			int i = EnchantmentHelper.getBlockEfficiency(entity);
			ItemStack itemstack = entity.getMainHandItem();
			if (i > 0 && !itemstack.isEmpty()) {
				miningSpeed += getEfficiencyBonus(miningSpeed, i);
			}
		}

		if (MobEffectUtil.hasDigSpeed(entity)) {
			miningSpeed *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(entity) + 1) * 0.2F;
		}

		if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
			float miningFatigueMultiplier = switch (entity.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
				case 0 -> 0.3F;
				case 1 -> 0.09F;
				case 2 -> 0.0027F;
				default -> 8.1E-4F;
			};
			miningSpeed *= miningFatigueMultiplier;
		}

		if (entity.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !EnchantmentHelper.hasAquaAffinity(entity)) {
			miningSpeed /= 5.0F;
		}

		if (!entity.onGround()) {
			miningSpeed /= 5.0F;
		}

		return miningSpeed;
	}
}