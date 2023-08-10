package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.survivalreimagined.event.SREventFactory;
import insane96mcp.survivalreimagined.module.movement.TerrainSlowdown;
import insane96mcp.survivalreimagined.module.sleeprespawn.tiredness.Tiredness;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, net.minecraftforge.common.extensions.IForgeLivingEntity {

    @Shadow public abstract Collection<MobEffectInstance> getActiveEffects();

    @Shadow public abstract boolean hasEffect(MobEffect pEffect);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect pEffect);

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

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

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void onPostDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(Tiredness.TIRED.get())) {
            //noinspection DataFlowIssue
            cir.setReturnValue(6 + (1 + this.getEffect(Tiredness.TIRED.get()).getAmplifier()));
        }
    }

    @Redirect(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V"))
    public void onResetFallDamageOnClimbables(LivingEntity instance) {
        if (instance.fallDistance > 0f)
            instance.causeFallDamage(instance.fallDistance, 1f, instance.damageSources().fall());
        instance.resetFallDistance();
    }
}
