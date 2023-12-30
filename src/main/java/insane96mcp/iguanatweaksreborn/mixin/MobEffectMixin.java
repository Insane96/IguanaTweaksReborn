package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.Tweaks;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEffect.class)
public class MobEffectMixin {

	@ModifyConstant(method = "isDurationEffectTick", constant = @Constant(intValue = 25, ordinal = 0))
	public int onPoisonTickDamage(int constant) {
		return Tweaks.getPoisonDamageSpeed();
	}
}
