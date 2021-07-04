package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
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

	@Shadow
	public int experienceTotal;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Shadow
	public abstract boolean isSpectator();

	@Inject(at = @At("RETURN"), method = "xpBarCap", cancellable = true)
	private void xpBarCap(CallbackInfoReturnable<Integer> callback) {
		if (Modules.experience.playerExperience.betterScalingLevels)
			callback.setReturnValue(3 * (this.experienceLevel + 1));
	}

	@Inject(at = @At("RETURN"), method = "getExperiencePoints(Lnet/minecraft/entity/player/PlayerEntity;)I", cancellable = true)
	private void getExperiencePoints(PlayerEntity player, CallbackInfoReturnable<Integer> callback) {
		if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !this.isSpectator() && Modules.experience.playerExperience.droppedExperienceOnDeath > 0d)
			callback.setReturnValue((int) (this.experienceTotal * Modules.experience.playerExperience.droppedExperienceOnDeath));
	}
}
