package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.combat.stats.Stats;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
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

    //Disable mobs arrow random bonus damage
    @Inject(method = "setEnchantmentEffectsFromEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setBaseDamage(D)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onSetMobArrowDamage(LivingEntity pShooter, float pVelocity, CallbackInfo ci) {
        this.setBaseDamage(pVelocity * 2.0F);
    }
}
