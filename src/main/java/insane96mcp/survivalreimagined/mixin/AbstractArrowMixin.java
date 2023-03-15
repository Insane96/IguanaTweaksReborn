package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.combat.feature.Stats;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.AFTER), method = "onHitEntity")
    public void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (Stats.disableArrowInvFrames())
            entityHitResult.getEntity().invulnerableTime = 0;
    }
}
