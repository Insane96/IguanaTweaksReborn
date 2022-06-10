package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
		super(p_19870_, p_19871_);
	}

	@ModifyConstant(constant = @Constant(intValue = 5), method = "isBlocking")
	private int blockingWindupTime(int ticks) {
		return Modules.combat.stats.removeShieldWindup ? 0 : ticks;
	}

	/*@Inject(at = @At(value = "JUMP", target = "Lnet/minecraft/world/entity/LivingEntity;playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V"), method = "hurt", cancellable = true)
	private void onHurtSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {

	}*/
}
