package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.combat.RegeneratingAbsorption;
import insane96mcp.iguanatweaksreborn.module.movement.TerrainSlowdown;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.tags.DamageTypeTags;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, net.minecraftforge.common.extensions.IForgeLivingEntity {

    @Shadow public abstract boolean hasEffect(MobEffect pEffect);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect pEffect);

    @Shadow public abstract float getAbsorptionAmount();

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getCurrentSwingDuration", at = @At("HEAD"), cancellable = true)
    private void onPostDamage(CallbackInfoReturnable<Integer> cir) {
        if (this.hasEffect(Tiredness.TIRED.get())) {
            //noinspection DataFlowIssue
            cir.setReturnValue(6 + (1 + this.getEffect(Tiredness.TIRED.get()).getAmplifier()));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"), method = "jumpFromGround")
    public boolean onSprintJumpCheck(LivingEntity instance) {
        if (instance.isSprinting() && Feature.isEnabled(TerrainSlowdown.class)) {
            float yRot = instance.getYRot() * ((float)Math.PI / 180F);
            float boost = 0.2f;
            boost *= (float) MCUtils.getMovementSpeedRatio(instance);
            instance.setDeltaMovement(instance.getDeltaMovement().add((-Mth.sin(yRot) * boost), 0.0D, (Mth.cos(yRot) * boost)));
            return false;
        }
        return instance.isSprinting();
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private float onCalculateAbsorption(float f1, DamageSource damageSource, float amount) {
        if (RegeneratingAbsorption.entityAbsorption() && (damageSource.getEntity() == null || damageSource.is(DamageTypeTags.BYPASSES_ARMOR))) {
            return amount;
        }
        return Math.max(amount - this.getAbsorptionAmount(), 0.0F);
    }

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAbsorptionAmount(F)V", ordinal = 1))
    private void onSetAbsorptionSecondTime(LivingEntity instance, float absorption) {
        //Cancel Mojang damaging absorption twice for some reason
    }

    @Redirect(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V"))
    public void onResetFallDamageOnClimbable(LivingEntity instance) {
        if (instance.fallDistance > 0f)
            instance.causeFallDamage(instance.fallDistance, 1f, instance.damageSources().fall());
        instance.resetFallDistance();
    }
}
