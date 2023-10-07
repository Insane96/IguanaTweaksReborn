package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.combat.RegeneratingAbsorption;
import insane96mcp.survivalreimagined.module.experience.PlayerExperience;
import insane96mcp.survivalreimagined.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

//Higher priority over ShieldsPlus. This makes this run first so ShieldPlus overrides this.
@Mixin(value = Player.class, priority = 1001)
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

	@ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
	private float onCalculateAbsorption(float f1, DamageSource damageSource, float amount) {
		if (RegeneratingAbsorption.entityAbsorption() && (damageSource.getEntity() == null || damageSource.is(DamageTypeTags.BYPASSES_ARMOR))) {
			return amount;
		}
		return Math.max(amount - this.getAbsorptionAmount(), 0.0F);
	}
}
