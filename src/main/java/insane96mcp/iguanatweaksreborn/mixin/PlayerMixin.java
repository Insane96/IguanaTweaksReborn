package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.event.ITREventFactory;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.PlayerExperience;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

//Higher priority over ShieldsPlus. This makes this run first so ShieldPlus overrides this.
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	@Shadow
	public int experienceLevel;

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


	@ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F"))
	public float onEnchantmentDamage(float original, Entity target) {
		Map<Enchantment, Integer> allEnchantments = this.getMainHandItem().getAllEnchantments();
		for (Enchantment enchantment : allEnchantments.keySet()) {
			original += EnchantmentsFeature.bonusDamageEnchantment(enchantment, allEnchantments.get(enchantment), this, target);
		}
		return original;
	}
}
