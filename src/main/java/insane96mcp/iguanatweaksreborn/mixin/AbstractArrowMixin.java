package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract boolean isCritArrow();

    @Shadow public abstract void setBaseDamage(double pBaseDamage);

    @Shadow private double baseDamage;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(at = @At(value = "STORE"), method = "onHitEntity", ordinal = 0)
    private float clampDeltaMovementLength(float deltaLength) {
        return Math.min(deltaLength, 10f);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isCritArrow()Z", ordinal = 0), method = "onHitEntity")
    private boolean onCritArrowCheck(AbstractArrow arrow) {
        if (Feature.isEnabled(Stats.class) && Stats.disableCritArrowsBonusDamage)
            return false;
        return this.isCritArrow();
    }

    //Disable mobs' arrow random bonus damage
    @Inject(method = "setEnchantmentEffectsFromEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setBaseDamage(D)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onSetMobArrowDamage(LivingEntity pShooter, float pVelocity, CallbackInfo ci) {
        this.setBaseDamage(pVelocity * this.baseDamage);
    }

    @WrapOperation(method = "onHitEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;baseDamage:D"))
    private double onSetArrowDamage(AbstractArrow instance, Operation<Double> original) {
        return original.call(instance) * Stats.arrowsDamageMultiplier;
    }

    @ModifyArg(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float onHurtDamage(float damage) {
        if (!Stats.decimalArrowsDamage)
            return damage;
        double l = this.getDeltaMovement().length();
        float newDamage = (float) Mth.clamp(l * this.baseDamage * Stats.arrowsDamageMultiplier, 0.0D, Integer.MAX_VALUE);
        if (this.isCritArrow() && !Stats.disableCritArrowsBonusDamage) {
            newDamage += this.random.nextFloat() * (newDamage / 2 + 2);
        }
        return newDamage;
    }

    @ModifyExpressionValue(method = "setEnchantmentEffectsFromEntity", at = @At(value = "CONSTANT", args = "doubleValue=0.5", ordinal = 0))
    public double powerBonusPerLevel(double pBaseDamage) {
        return EnchantmentsFeature.powerEnchantmentDamage;
    }

    @ModifyExpressionValue(method = "setEnchantmentEffectsFromEntity", at = @At(value = "CONSTANT", args = "doubleValue=0.5", ordinal = 1))
    public double powerBonusFlat(double pBaseDamage) {
        return EnchantmentsFeature.powerEnchantmentDamage != 0.5d ? 0 : pBaseDamage;
    }
}
