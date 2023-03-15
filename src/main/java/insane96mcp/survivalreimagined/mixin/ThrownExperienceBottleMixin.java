package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.OtherExperience;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownExperienceBottle.class)
public class ThrownExperienceBottleMixin {

	@Inject(at = @At("HEAD"), method = "onHit")
	public void onHit(HitResult hitResult, CallbackInfo callbackInfo) {
		OtherExperience.onXpBottleHit((ThrownExperienceBottle) (Object) this);
	}
}
