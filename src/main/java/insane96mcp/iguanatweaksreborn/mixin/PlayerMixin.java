package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import insane96mcp.iguanatweaksreborn.event.ITREventFactory;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.PlayerExperience;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.healthregen.HealthRegen;
import insane96mcp.iguanatweaksreborn.module.misc.Tweaks;
import insane96mcp.iguanatweaksreborn.module.world.Nether;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

//Higher priority over ShieldsPlus. This makes this run first so ShieldPlus overrides this.
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	@Shadow
	public int experienceLevel;

	@Shadow public abstract void remove(RemovalReason pReason);

	@Shadow public abstract void resetAttackStrengthTicker();

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(at = @At("RETURN"), method = "getXpNeededForNextLevel", cancellable = true)
	private void xpBarCap(CallbackInfoReturnable<Integer> callback) {
		int exp = PlayerExperience.getBetterScalingLevel(this.experienceLevel);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@Inject(at = @At("HEAD"), method = "getExperienceReward", cancellable = true)
	private void getExperiencePoints(CallbackInfoReturnable<Integer> callback) {
		int exp = PlayerExperience.getExperienceOnDeath((Player) (Object) this, false);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onPlayerAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.BEFORE), argsOnly = true)
	public float onAttackAmount(float amount, DamageSource source) {
		return ITREventFactory.onPlayerAttack(this, source, amount);
	}

	//Changes efficiency formula
	@ModifyVariable(method = "getDigSpeed", ordinal = 0, at = @At(value = "STORE", ordinal = 1), remap = false)
	private float changeEfficiencyFormula(float efficiency, BlockState p_36282_, @Nullable BlockPos pos) {
		if (!EnchantmentsFeature.isBetterEfficiencyFormula())
			return efficiency;
		int lvl = EnchantmentHelper.getBlockEfficiency((Player) (Object) this);
		//Remove vanilla efficiency
		efficiency -= (float)(lvl * lvl + 1);
		return efficiency + EnchantmentsFeature.getEfficiencyBonus(efficiency, lvl);
	}

	/*@ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
	private float onCalculateAbsorption(float f1, DamageSource damageSource, float amount) {
		if (RegeneratingAbsorption.damageTypeTagOnly() && (damageSource.getEntity() == null || damageSource.is(DamageTypeTags.BYPASSES_ARMOR))) {
			return amount;
		}
		return Math.max(amount - this.getAbsorptionAmount(), 0.0F);
	}*/

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 2), argsOnly = true, ordinal = 0)
	public float onPreAbsorptionCalculation(float amount, DamageSource damageSource) {
		return ITREventFactory.onLivingHurtPreAbsorption(this, damageSource, amount);
	}

	@ModifyConstant(method = "attack", constant = @Constant(floatValue = 0.2f, ordinal = 0))
	public float attackStrengthAtMaxCooldown(float value) {
        return Stats.noDamageWhenSpamming() ? 0f : value;
    }

	@ModifyConstant(method = "attack", constant = @Constant(floatValue = 0.8f, ordinal = 0))
	public float attackStrengthAtFullSwing(float value) {
		return Stats.noDamageWhenSpamming() ? 1f : value;
	}

	@ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	public float changeSweepingDamage(float original, @Local(ordinal = 0) float f) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return original;
		return f;
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getSweepHitBox(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/AABB;"))
	public AABB changeSweepingHitbox(AABB original) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return original;
		int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, this);
		return original.inflate(lvl * 0.25f, lvl * 0.1f, lvl * 0.25f);
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEntityReach()D"))
	public double increaseSweepingReach(double original) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return original;
		int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, this);
		return original + lvl * 0.25f;
	}

	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F"))
	public float onEnchantmentDamage(float original, Entity target) {
		Map<Enchantment, Integer> allEnchantments = this.getMainHandItem().getAllEnchantments();
		for (Enchantment enchantment : allEnchantments.keySet()) {
			original += EnchantmentsFeature.bonusDamageEnchantment(enchantment, allEnchantments.get(enchantment), this, target);
		}
		return original;
	}

	/*@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z", ordinal = 1))
	public boolean allowSweepingOffGround(boolean original) {
		return true;
	}*/

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/world/entity/LivingEntity;)I"))
	public void storeNewFlag3(Entity pTarget, CallbackInfo ci, @Local(ordinal = 0) boolean flag, @Share("flag3") LocalBooleanRef flag3) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return;
		if (flag) {
			ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
			flag3.set(itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP));
		}
	}

	@ModifyVariable(method = "attack", ordinal = 3, at = @At("LOAD"))
	public boolean onFlag3Check(boolean original, @Share("flag3") LocalBooleanRef flag3) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return original;
		return flag3.get();
	}

	@ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
	public boolean onCheckPeacefulRegen(boolean original) {
		return original && !HealthRegen.peacefulHunger;
	}

	@ModifyExpressionValue(method = "attack",at = @At(value = "CONSTANT", args = "doubleValue=0.4000000059604645"))
	public double onSweepKnockbackStrength(double original, @Local(name = "i") float i) {
		if (!Stats.sweepingOverhaul
				|| !Feature.isEnabled(Stats.class))
			return original;
		return i * 0.5F;
	}

	@ModifyExpressionValue(method = "turtleHelmetTick", at = @At(value = "CONSTANT", args = "intValue=200"))
	public int onTurtleHelmetTick(int original) {
		if (!Feature.isEnabled(Tweaks.class))
			return original;
		return Tweaks.turtleHelmetWaterBreathingTime;
	}

	@ModifyExpressionValue(method = "getPortalWaitTime", at = @At(value = "CONSTANT", args = "intValue=80"))
	public int getPortalWaitTime(int original) {
		if (!Feature.isEnabled(Nether.class))
			return original;
		return Nether.portalWaitTime;
	}

	/*@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSwimming()Z"))
	public boolean onTravelSwimCheck(boolean original) {
		if (this.isSwimming() && !this.isPassenger()) {
			this.setDeltaMovement(Vec3.ZERO);
			double d3 = this.getLookAngle().y;
			double d4 = d3 < -0.2D ? 0.085D : 0.06D;
			if (d3 <= 0.0D || !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0D - 0.1D, this.getZ())).getFluidState().isEmpty()) {
				Vec3 vec3 = this.getDeltaMovement();
				this.setDeltaMovement(vec3.add(0.0D, (d3 - vec3.y) * d4, 0.0D));
			}
		}
		return false;
	}*/

}
