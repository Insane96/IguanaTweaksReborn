package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.combat.AttackInvincibility;
import insane96mcp.survivalreimagined.module.combat.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract boolean isCritArrow();

    @Shadow public abstract void setBaseDamage(double pBaseDamage);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER), method = "onHitEntity")
    private void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (AttackInvincibility.disableArrowInvFrames()/* && this.getOwner() instanceof Player*/) {
            entityHitResult.getEntity().invulnerableTime = 0;
        }
    }

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

    @Inject(method = "setEnchantmentEffectsFromEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setBaseDamage(D)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onSetMobArrowDamage(LivingEntity pShooter, float pVelocity, CallbackInfo ci) {
        this.setBaseDamage(pVelocity * 2.0F);
    }
}
