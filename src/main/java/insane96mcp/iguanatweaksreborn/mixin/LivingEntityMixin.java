package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.iguanatweaksreborn.event.ITREventFactory;
import insane96mcp.iguanatweaksreborn.module.combat.RegeneratingAbsorption;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection.IProtectionEnchantment;
import insane96mcp.iguanatweaksreborn.module.movement.Swimming;
import insane96mcp.iguanatweaksreborn.module.movement.TerrainSlowdown;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableFloat;
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

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow public abstract void swing(InteractionHand pHand);

    @Shadow public abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 pDeltaMovement, float pFriction);

    @Shadow @Nullable protected abstract SoundEvent getDeathSound();

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getCurrentSwingDuration", at = @At("RETURN"), cancellable = true)
    private void onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> cir) {
        int ret = cir.getReturnValue();
        //noinspection DataFlowIssue
        if (this.hasEffect(Tiredness.TIRED.get()) && this.getEffect(Tiredness.TIRED.get()).getAmplifier() > 2)
            cir.setReturnValue(ret + 1);
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

    @ModifyVariable(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingAttack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.BEFORE), argsOnly = true)
    public float onAttackAmount(float amount, DamageSource source) {
        return ITREventFactory.onLivingAttack((LivingEntity) (Object) this, source, amount);
    }

    /*@ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private float onCalculateAbsorption(float f1, DamageSource damageSource, float amount) {
        if (RegeneratingAbsorption.damageTypeTagOnly() && damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            return amount;
        }
        return Math.max(amount - this.getAbsorptionAmount(), 0.0F);
    }

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAbsorptionAmount(F)V", ordinal = 1))
    private void onSetAbsorptionSecondTime(LivingEntity instance, float absorption) {
        //Cancel Mojang damaging absorption twice for some reason
    }*/

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "STORE", ordinal = 2), argsOnly = true, ordinal = 0)
    public float onPreAbsorptionCalculation(float amount, DamageSource damageSource) {
        return ITREventFactory.onLivingHurtPreAbsorption((LivingEntity) (Object) this, damageSource, amount);
    }

    @Redirect(method = "handleOnClimbable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;resetFallDistance()V"))
    public void onResetFallDamageOnClimbable(LivingEntity instance) {
        if (instance.fallDistance > 0f)
            instance.causeFallDamage(instance.fallDistance, 1f, instance.damageSources().fall());
        instance.resetFallDistance();
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V"))
    public void onPlayHurtSound(LivingEntity instance, DamageSource pSource, Operation<Void> original) {
        if (instance.getPersistentData().contains(RegeneratingAbsorption.NO_HURT_SOUND_TAG))
            return;
        original.call(instance, pSource);
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I"), cancellable = true)
    public void onGetDamageProtection(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        if (!Feature.isEnabled(EnchantmentsFeature.class)
                || !EnchantmentsFeature.replaceProtectionEnchantments
                || damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return;
        MutableFloat damageReduction = new MutableFloat();
        for (ItemStack stack : this.getArmorSlots()) {
            stack.getAllEnchantments().forEach((enchantment, lvl) -> {
                if (enchantment instanceof IProtectionEnchantment protectionEnchantment && ((IProtectionEnchantment) enchantment).isSourceReduced(damageSource))
                    damageReduction.add(protectionEnchantment.getDamageReduction(lvl));
            });
        }
        if (damageReduction.getValue() == 0f)
            return;
        cir.setReturnValue(damageAmount - (damageAmount * Math.min(damageReduction.getValue(), 0.8f)));
    }

    @WrapOperation(method = "handleDamageEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    public void onPlayHurtSoundClientSide(LivingEntity instance, SoundEvent soundEvent, float volume, float pitch, Operation<Void> original, DamageSource source) {
        if (RegeneratingAbsorption.canDamageAbsorption(source) && instance.getPersistentData().getFloat(RegeneratingAbsorption.REGEN_ABSORPTION_TAG) > 0)
            return;
        original.call(instance, soundEvent, volume, pitch);
    }

    @Inject(method = "decreaseAirSupply", at = @At("RETURN"), cancellable = true)
    public void onDecreaseAirSupply(int pCurrentAir, CallbackInfoReturnable<Integer> cir) {
        if (!Feature.isEnabled(EnchantmentsFeature.class)
                || !EnchantmentsFeature.respirationNerf)
            return;
        int respirationLvl = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);
        if (respirationLvl > 0
                && this.random.nextFloat() < 1f / (1 + (respirationLvl / 2f)))
            cir.setReturnValue(pCurrentAir);
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isAffectedByFluids()Z"))
    public boolean onJumpWhenSwimmingCheck(boolean original) {
        if (!Swimming.shouldPreventFastSwimUpWithJump())
            return original;
        return original && !this.isSwimming();
    }

    /*@WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 checkCollideHorizontallyAndDamage(LivingEntity instance, Vec3 pTravelVector, float pFriction, Operation<Vec3> original) {
        double horizontalDistance = this.getDeltaMovement().horizontalDistance();
        Vec3 originalResult = original.call(instance, pTravelVector, pFriction);
        if (this.horizontalCollision && !this.level().isClientSide) {
            double length = horizontalDistance - this.getDeltaMovement().horizontalDistance();
            if (length > 0.2f)
                this.hurt(this.damageSources().flyIntoWall(), (float) ((length - 0.2f) * 10f));
        }
        return originalResult;
    }*/
}
