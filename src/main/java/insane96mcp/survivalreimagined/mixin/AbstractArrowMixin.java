package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.combat.feature.Stats;
import insane96mcp.survivalreimagined.network.message.SyncInvulnerableTimeMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Shadow public abstract boolean isCritArrow();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER), method = "onHitEntity")
    private void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (Stats.disableArrowInvFrames()) {
            entityHitResult.getEntity().invulnerableTime = 0;
            SyncInvulnerableTimeMessage.sync((ServerLevel) entityHitResult.getEntity().level, entityHitResult.getEntity(), 0);
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
}
