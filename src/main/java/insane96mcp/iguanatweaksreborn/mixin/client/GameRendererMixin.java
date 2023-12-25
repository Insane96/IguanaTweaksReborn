package insane96mcp.iguanatweaksreborn.mixin.client;

import insane96mcp.iguanatweaksreborn.module.client.Light;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@SuppressWarnings("ConstantConditions")
	@Inject(at = @At("RETURN"), method = "getNightVisionScale", cancellable = true)
	private static void getNightVisionScale(LivingEntity livingEntity, float partialTicks, CallbackInfoReturnable<Float> callback) {
		if (!Light.shouldDisableNightVisionFlashing())
			return;
		int duration = livingEntity.getEffect(MobEffects.NIGHT_VISION).getDuration();
		callback.setReturnValue(duration > Light.NIGHT_VISION_FADE_OUT_AT || duration == -1 ?
				1.0f :
				((float)duration - partialTicks) * (1f / Light.NIGHT_VISION_FADE_OUT_AT));
	}
}
