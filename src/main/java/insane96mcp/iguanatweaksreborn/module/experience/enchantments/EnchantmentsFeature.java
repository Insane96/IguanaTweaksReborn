package insane96mcp.iguanatweaksreborn.module.experience.enchantments;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.FireAspect;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.IEnchantmentTooltip;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.Knockback;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BaneOfSSSS;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.BonusDamageEnchantment;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.Sharpness;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.damage.Smite;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection.*;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatistics;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStatsReloadListener;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Label(name = "Enchantments", description = "Changes to some enchantments related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends JsonFeature {
	public static final RegistryObject<Enchantment> SHARPNESS = ITRRegistries.ENCHANTMENTS.register("sharpness", Sharpness::new);
	public static final RegistryObject<Enchantment> SMITE = ITRRegistries.ENCHANTMENTS.register("smite", Smite::new);
	public static final RegistryObject<Enchantment> BANE_OF_SSSSS = ITRRegistries.ENCHANTMENTS.register("bane_of_sssss", BaneOfSSSS::new);
	public static final RegistryObject<Enchantment> FIRE_ASPECT = ITRRegistries.ENCHANTMENTS.register("fire_aspect", FireAspect::new);
	public static final RegistryObject<Enchantment> KNOCKBACK = ITRRegistries.ENCHANTMENTS.register("knockback", Knockback::new);
	public static final RegistryObject<Enchantment> PROTECTION = ITRRegistries.ENCHANTMENTS.register("protection", OverallProtection::new);
	public static final RegistryObject<Enchantment> BLAST_PROTECTION = ITRRegistries.ENCHANTMENTS.register("blast_protection", BlastProtection::new);
	public static final RegistryObject<Enchantment> FIRE_PROTECTION = ITRRegistries.ENCHANTMENTS.register("fire_protection", FireProtection::new);
	public static final RegistryObject<Enchantment> PROJECTILE_PROTECTION = ITRRegistries.ENCHANTMENTS.register("projectile_protection", ProjectileProtection::new);
	public static final RegistryObject<Enchantment> FEATHER_FALLING = ITRRegistries.ENCHANTMENTS.register("feather_falling", FeatherFalling::new);
	public static final EnchantmentCategory WEAPONS_CATEGORY = EnchantmentCategory.create("itr_weapons", item -> item instanceof SwordItem || item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem || item instanceof HoeItem);
	public static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	@Config
	@Label(name = "Infinity overhaul", description = "Infinity can go up to level 4. Each level makes an arrow have 1 in level+1 chance to not consume.")
	public static Boolean infinityOverhaul = true;
	@Config
	@Label(name = "Less unbreakable unbreaking", description = "Unbreaking chance to not consume durability is changed from 50%/66.7%/75%/80%/... to 25%/45%/60%/70%/... (at levels I/II/III/IV)")
	public static Boolean unbreakingOverhaul = true;
	@Config
	@Label(name = "Small Thorns Overhaul", description = "Thorns is no longer compatible with other protections, but deals damage every time (higher levels deal more damage) and no longer damages items.")
	public static Boolean thornsOverhaul = true;

	@Config
	@Label(name = "Better Efficiency Formula", description = "Change the efficiency formula from tool_efficiency+(lvl*lvl+1) to tool_efficiency * (1 + (0.5*lvl))")
	public static Boolean changeEfficiencyFormula = true;

	@Config(min = 0d, max = 2d)
	@Label(name = "Power Enchantment Damage", description = "Set arrow's damage increase with the Power enchantment (vanilla is 0.5). Set to 0.5 to disable.")
	public static Double powerEnchantmentDamage = 0.35d;

	@Config
	@Label(name = "Prevent farmland trampling with Feather Falling")
	public static Boolean preventFarmlandTramplingWithFeatherFalling = true;
	@Config
	@Label(name = "Replace protection enchantments", description = "If true, vanilla protection enchantments are replaced with mod's ones. To re-enable vanilla enchantments refer to `disabled_enchantments.json`.\n" +
			"Protection has only one level, protects 6% per level and is treasure. Other protections work the same except for projectile that reduces the sight range of mobs by 2% per level. Feather falling protects for 16% per level instead of 12%.")
	public static Boolean replaceProtectionEnchantments = true;
	@Config
	@Label(name = "Replace damaging enchantments", description = """
            If true, vanilla damaging enchantments (such as smite or sharpness) are replaced with mod's ones. To re-enable vanilla enchantments refer to `disabled_enchantments.json`.
            Changes to damaging enchantments:
            Enchantments deal bonus damage based off the item's attack damage. So Sharpness on a Sword adds less damage than Sharpness on an Axe.
            Sharpness deals +0.75 damage per level
            Smite deals +1.25 damage per level to undead
            Bane of Arthropods has been replaced with Bane of SSSSS that deals +1.25 damage per level to arthropods and creepers and applies slowness""")
	public static Boolean replaceDamagingEnchantments = true;

	public static final ArrayList<IdTagMatcher> DISABLED_ENCHANTMENTS_DEFAULT = new ArrayList<>(List.of(
			IdTagMatcher.newId("minecraft:mending"),
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
			IdTagMatcher.newId("allurement:reforming"),
			IdTagMatcher.newId("allurement:alleviating")

	));
	public static final ArrayList<IdTagMatcher> disabledEnchantments = new ArrayList<>();

	public EnchantmentsFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);

		JSON_CONFIGS.add(new JsonConfig<>("disabled_enchantments.json", disabledEnchantments, DISABLED_ENCHANTMENTS_DEFAULT, IdTagMatcher.LIST_TYPE));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);
		if (infinityOverhaul)
			Enchantments.INFINITY_ARROWS.rarity = Enchantment.Rarity.RARE;
		else
			Enchantments.INFINITY_ARROWS.rarity = Enchantment.Rarity.VERY_RARE;

		if (thornsOverhaul)
			Enchantments.THORNS.rarity = Enchantment.Rarity.RARE;
		else
			Enchantments.THORNS.rarity = Enchantment.Rarity.VERY_RARE;
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
			return 1 - 1 / (1 + lvl * 0.5f);
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
			return toolEfficiency * (lvl * 0.5f);
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
		if (attacker instanceof Player player)
			knockback *= player.getAttackStrengthScale(0.5f);
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

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity attacker))
			return;

		Map<Enchantment, Integer> allEnchantments = attacker.getMainHandItem().getAllEnchantments();
		for (Enchantment enchantment : allEnchantments.keySet()) {
			bonusDamageEnchantment(enchantment, allEnchantments.get(enchantment), attacker, event.getEntity(), event);
		}
	}

	public void bonusDamageEnchantment(Enchantment enchantment, int lvl, LivingEntity attacker, LivingEntity target, LivingHurtEvent event) {
		if (!(enchantment instanceof BonusDamageEnchantment bonusDamageEnchantment))
			return;
		float damageBonus = bonusDamageEnchantment.getDamageBonus(attacker, target, attacker.getMainHandItem(), lvl);
		event.setAmount(event.getAmount() + damageBonus);
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

	@SubscribeEvent
	public void onArrowSpawn(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof AbstractArrow arrow))
			return;
		if (!arrow.shotFromCrossbow())
			processBow(arrow);
	}

	private static void processBow(AbstractArrow arrow) {
		if (isEnabled(Stats.class) && Stats.bowsArrowsBaseDamage != 2d) {
			arrow.setBaseDamage(arrow.getBaseDamage() - (2d - Stats.bowsArrowsBaseDamage));
		}
		if (powerEnchantmentDamage != 0.5d && arrow.getOwner() instanceof LivingEntity) {
			int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, (LivingEntity) arrow.getOwner());
			if (powerLevel == 0)
				return;
			double powerReduction = 0.5d - powerEnchantmentDamage;
			arrow.setBaseDamage(arrow.getBaseDamage() - (powerLevel * powerReduction + 0.5d));
		}
	}

	public static boolean isInfinityOverhaulEnabled() {
		return Feature.isEnabled(EnchantmentsFeature.class) && infinityOverhaul;
	}

	public static int getEnchantmentValue(ItemStack stack) {
		for (ItemStatistics itemStatistics : ItemStatsReloadListener.STATS) {
			if (itemStatistics.enchantability() != null && itemStatistics.item().matchesItem(stack))
				return itemStatistics.enchantability();
		}
		return stack.getEnchantmentValue();
	}

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!this.isEnabled()
				|| !event.getItemStack().isEnchanted()
				|| !Screen.hasShiftDown())
			return;

		Map<Enchantment, Integer> allEnchantments = event.getItemStack().getAllEnchantments();
		for (Enchantment enchantment : allEnchantments.keySet()) {
			if (!(enchantment instanceof IEnchantmentTooltip enchantmentTooltip))
				continue;
			int lvl = allEnchantments.get(enchantment);
			event.getToolTip().add(enchantmentTooltip.getTooltip(event.getItemStack(), lvl));
		}
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