package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.event.SREventFactory;
import insane96mcp.survivalreimagined.module.movement.feature.TerrainSlowdown;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"), method = "jumpFromGround")
    public boolean onSprintJumpCheck(LivingEntity instance) {
        if (instance.isSprinting() && Feature.isEnabled(TerrainSlowdown.class)) {
            float yRot = instance.getYRot() * ((float)Math.PI / 180F);
            float boost = 0.2f;
            boost *= MCUtils.getMovementSpeedRatio(instance);
            instance.setDeltaMovement(instance.getDeltaMovement().add((-Mth.sin(yRot) * boost), 0.0D, (Mth.cos(yRot) * boost)));
            return false;
        }
        return instance.isSprinting();
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V"))
    private void onPostDamage(DamageSource damageSource, float amount, CallbackInfo ci) {
        SREventFactory.onPostHurtEntity((LivingEntity)(Object)this, damageSource, amount);
    }
}
