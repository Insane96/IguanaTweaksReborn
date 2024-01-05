package insane96mcp.iguanatweaksreborn.module.experience.enchantments;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.BaneOfSSSS;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Enchantments", description = "Changes to some enchantments related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantmentsFeature extends JsonFeature {
	public static final RegistryObject<Enchantment> BANE_OF_SSSSS = ITRRegistries.ENCHANTMENTS.register("bane_of_sssss", BaneOfSSSS::new);
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
	@Config
	@Label(name = "Specific damaging enchantments bonus damage", description = "Changes the bonus damage of type based damage enchantments (e.g. Smite). Vanilla is 2.5")
	public static Double typeBasedDamageEnchantmentBonus = 2.0d;

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
	public void onLivingAttack(LivingHurtEvent event) {
		if (!this.isEnabled()
				|| !(event.getSource().getEntity() instanceof LivingEntity attacker))
			return;

		baneOfSssssOnAttack(attacker, event.getEntity(), event);
	}

	public void baneOfSssssOnAttack(LivingEntity attacker, LivingEntity entity, LivingHurtEvent event) {
		if (!(entity instanceof Creeper))
			return;
		int lvl = attacker.getMainHandItem().getEnchantmentLevel(BANE_OF_SSSSS.get());
		if (lvl == 0)
			return;

		event.setAmount((event.getAmount() + 2.5f * lvl));
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