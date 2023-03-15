package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.effect.Vigour;
import insane96mcp.survivalreimagined.module.combat.feature.Shields;
import insane96mcp.survivalreimagined.module.experience.feature.PlayerExperience;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.ExhaustionIncrease;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Tiredness;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
		int exp = PlayerExperience.getExperienceOnDeath((Player) (Object) this);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@Inject(at = @At("HEAD"), method = "causeFoodExhaustion")
	private void causeFoodExhaustion(float amount, CallbackInfo ci) {
		Tiredness.onFoodExhaustion((Player) (Object) this, amount);
	}

	@ModifyVariable(method = "causeFoodExhaustion", argsOnly = true, at = @At("HEAD"))
	private float applyHungerToFoodExhaustion(float amount) {
		float newAmount = ExhaustionIncrease.increaseHungerEffectiveness((Player) (Object) this, amount);
		newAmount = Vigour.decreaseExhaustionConsumption((Player) (Object) this, newAmount);
		return newAmount;
	}

	@Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
	private void onDisableShield(boolean efficiencyAffected, CallbackInfo callbackInfo) {
		if (Shields.combatTestShieldDisabling()) {
			callbackInfo.cancel();
			this.getCooldowns().addCooldown(this.getUseItem().getItem(), 32);
			this.stopUsingItem();
		}
	}

	/*@ModifyVariable(method = "getDigSpeed", ordinal = 0, at = @At(value = "STORE", ordinal = 1), remap = false)
	private float changeEfficiencyFormula(float efficiency, BlockState p_36282_, @Nullable BlockPos pos) {
		int i = EnchantmentHelper.getBlockEfficiency((Player) (Object) this);
		efficiency -= (float)(i * i + 1);
		efficiency += i * 5;
		return efficiency;
	}*/

	@Shadow
	public abstract ItemCooldowns getCooldowns();
}
