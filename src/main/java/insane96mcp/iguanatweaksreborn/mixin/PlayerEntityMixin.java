package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow
	public int experienceLevel;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Shadow
	public abstract boolean isSpectator();

	@Inject(at = @At("RETURN"), method = "xpBarCap", cancellable = true)
	private void xpBarCap(CallbackInfoReturnable<Integer> callback) {
		int exp = Modules.experience.playerExperience.getBetterScalingLevel(this.experienceLevel);
		if (exp != -1)
			callback.setReturnValue(exp);
	}

	@Inject(at = @At("HEAD"), method = "getExperiencePoints(Lnet/minecraft/entity/player/PlayerEntity;)I", cancellable = true)
	private void getExperiencePoints(PlayerEntity player, CallbackInfoReturnable<Integer> callback) {
		int exp = Modules.experience.playerExperience.getExperienceOnDeath((PlayerEntity) (Object) this);
		if (exp != -1)
			callback.setReturnValue(exp);
	}
}
