package insane96mcp.iguanatweaksreborn.module.experience.enchantments;

import com.mojang.blaze3d.platform.InputConstants;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.*;
import insane96mcp.iguanatweaksreborn.network.message.JumpMidAirMessage;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Enchantments", description = "Change some enchantments related stuff and adds new enchantments.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends JsonFeature {

	public static final TagKey<EntityType<?>> WATER_COOLANT_AFFECTED = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "water_coolant_affected"));
	public static final RegistryObject<Enchantment> MAGNETIC = ITRRegistries.ENCHANTMENTS.register("magnetic", Magnetic::new);
	public static final RegistryObject<Enchantment> MAGIC_PROTECTION = ITRRegistries.ENCHANTMENTS.register("magic_protection", MagicProtection::new);
	public static final RegistryObject<Enchantment> MELEE_PROTECTION = ITRRegistries.ENCHANTMENTS.register("melee_protection", MeleeProtection::new);
	public static final RegistryObject<Enchantment> BLASTING = ITRRegistries.ENCHANTMENTS.register("blasting", Blasting::new);
	public static final RegistryObject<Enchantment> EXPANDED = ITRRegistries.ENCHANTMENTS.register("expanded", Expanded::new);
	public static final RegistryObject<Enchantment> STEP_UP = ITRRegistries.ENCHANTMENTS.register("step_up", StepUp::new);
	public static final RegistryObject<Enchantment> BANE_OF_SSSSS = ITRRegistries.ENCHANTMENTS.register("bane_of_sssss", BaneOfSSSS::new);
	public static final RegistryObject<Enchantment> WATER_COOLANT = ITRRegistries.ENCHANTMENTS.register("water_coolant", WaterCoolant::new);
	public static final RegistryObject<Enchantment> SMARTNESS = ITRRegistries.ENCHANTMENTS.register("smartness", Smartness::new);
	public static final RegistryObject<Enchantment> MA_JUMP = ITRRegistries.ENCHANTMENTS.register("ma_jump", DoubleJump::new);
	public static final RegistryObject<Enchantment> GRAVITY_DEFYING = ITRRegistries.ENCHANTMENTS.register("gravity_defying", GravityDefying::new);
	public static final RegistryObject<Enchantment> CRITICAL = ITRRegistries.ENCHANTMENTS.register("critical", Critical::new);
	public static final RegistryObject<Enchantment> SWIFT_STRIKE = ITRRegistries.ENCHANTMENTS.register("swift_strike", SwiftStrike::new);
	public static final RegistryObject<Enchantment> HEALTHY = ITRRegistries.ENCHANTMENTS.register("healthy", Healthy::new);

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
	@Label(name = "Better Efficiency Formula", description = "Change the efficiency formula from tool_efficiency+(lvl*lvl+1) to tool_efficiency * (0.15 * (lvl * lvl + 1))")
	public static Boolean changeEfficiencyFormula = true;

	@Config(min = 0d, max = 2d)
	@Label(name = "Power Enchantment Damage", description = "Set arrow's damage increase with the Power enchantment (vanilla is 0.5). Set to 0.5 to disable.")
	public static Double powerEnchantmentDamage = 0.35d;

	@Config
	@Label(name = "Prevent farmland trampling with Feather Falling")
	public static Boolean preventFarmlandTramplingWithFeatherFalling = true;
	@Config
	@Label(name = "Buff Feather Falling", description = "Increases the damage protection from Feather Falling. From 12% per level to 16% per level")
	public static Boolean buffFeatherFalling = true;

	public static final ArrayList<IdTagMatcher> DISABLED_ENCHANTMENTS_DEFAULT = new ArrayList<>(List.of(
			IdTagMatcher.newId("minecraft:protection"),
			IdTagMatcher.newId("minecraft:mending"),
			IdTagMatcher.newId("minecraft:bane_of_arthropods"),
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
			/*float baseEfficiency = 0.15f;
			return toolEfficiency * (baseEfficiency * (lvl * lvl + 1));*/
			return toolEfficiency * (lvl * 0.5f);
		}
		else {
			return lvl * lvl + 1;
		}
	}

	public static boolean isThornsOverhaul() {
		return Feature.isEnabled(EnchantmentsFeature.class) && thornsOverhaul;
	}

    @SubscribeEvent
	public void onAttributeModifiers(ItemAttributeModifierEvent event) {
		if (!this.isEnabled())
			return;

		StepUp.applyAttributeModifier(event);
		GravityDefying.applyAttributeModifier(event);
		Healthy.applyAttributeModifier(event);
		SwiftStrike.applyAttributeModifier(event);
		MeleeProtection.applyAttributeModifier(event);
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;

		Magnetic.tryPullItems(event.getEntity());
	}

	@SubscribeEvent
	public void onEffectAdded(MobEffectEvent.Added event) {
		if (!this.isEnabled())
			return;

		MagicProtection.reduceBadEffectsDuration(event.getEntity(), event.getEffectInstance());
	}

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		event.setNewSpeed(event.getNewSpeed() + Blasting.getMiningSpeedBoost(event.getEntity(), event.getState()));
	}

	@SubscribeEvent
	public void onExperienceDropped(LivingExperienceDropEvent event) {
		if (!this.isEnabled()
				|| event.getAttackingPlayer() == null)
			return;
		int lvl = EnchantmentHelper.getEnchantmentLevel(SMARTNESS.get(), event.getAttackingPlayer());
		if (lvl > 0)
			event.setDroppedExperience(Smartness.getIncreasedExperience(event.getEntity().getRandom(), lvl, event.getDroppedExperience()));
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockExperienceDropped(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;
		int lvl = EnchantmentHelper.getEnchantmentLevel(SMARTNESS.get(), event.getPlayer());
		if (lvl > 0)
			event.setExpToDrop(Smartness.getIncreasedExperience(event.getLevel().getRandom(), lvl, event.getExpToDrop()));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onRenderLevel(RenderLevelStageEvent event) {
		if (!this.isEnabled())
			return;

		Expanded.applyDestroyAnimation(event);
	}

	//Priority high: run before Timber Trees
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!this.isEnabled())
			return;

		HitResult pick = event.getPlayer().pick(event.getPlayer().getEntityReach() + 0.5d, 1f, false);
		if (pick instanceof BlockHitResult blockHitResult)
			Expanded.apply(event.getPlayer(), event.getPlayer().level(), event.getPos(), blockHitResult.getDirection(), event.getState());
	}

	@SubscribeEvent
	public void onLivingAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity attacker))
			return;

		baneOfSssssOnAttack(attacker, event.getEntity(), event);
		waterCoolantOnAttack(attacker, event.getEntity(), event);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onCriticalHit(CriticalHitEvent event) {
		if (!this.isEnabled())
			return;

		int lvl = event.getEntity().getMainHandItem().getEnchantmentLevel(CRITICAL.get());
		if (lvl <= 0)
			return;
		event.setDamageModifier(Critical.getCritAmount(lvl, event.getDamageModifier()));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onJump(InputEvent.Key event) {
		if (!this.isEnabled()
				|| Minecraft.getInstance().player == null
				|| event.getAction() != InputConstants.PRESS
				|| event.getKey() != Minecraft.getInstance().options.keyJump.getKey().getValue())
			return;

		LocalPlayer player = Minecraft.getInstance().player;
		if (DoubleJump.extraJump(player)) {
			JumpMidAirMessage.jumpMidAir(player);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event) {
		if (!this.isEnabled())
			return;

		if (event.getEntity() instanceof LocalPlayer player)
			player.getPersistentData().putInt("double_jumps", 0);
		GravityDefying.applyFallDamageReduction(event);
	}

	public void baneOfSssssOnAttack(LivingEntity attacker, LivingEntity entity, LivingHurtEvent event) {
		if (!(entity instanceof Creeper))
			return;
		int lvl = attacker.getMainHandItem().getEnchantmentLevel(BANE_OF_SSSSS.get());
		if (lvl == 0)
			return;

		event.setAmount((event.getAmount() + 2.5f * lvl));
	}

	public void waterCoolantOnAttack(LivingEntity attacker, LivingEntity entity, LivingHurtEvent event) {
		if (!Utils.isEntityInTag(entity, WATER_COOLANT_AFFECTED))
			return;

		int lvl = attacker.getMainHandItem().getEnchantmentLevel(WATER_COOLANT.get());
		if (lvl == 0)
			return;

		float bonusDamage = 2.5f * lvl;
		event.setAmount((event.getAmount() + bonusDamage));
	}

	@SubscribeEvent
	public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (!this.isEnabled()
				|| !preventFarmlandTramplingWithFeatherFalling
				|| !(event.getEntity() instanceof LivingEntity entity)
				|| EnchantmentHelper.getEnchantmentLevel(Enchantments.FALL_PROTECTION, entity) <= 0)
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

	public static boolean isFeatherFallingBuffed() {
		return Feature.isEnabled(EnchantmentsFeature.class) && buffFeatherFalling;
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