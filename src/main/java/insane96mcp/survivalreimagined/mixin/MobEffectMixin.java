package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.hungerhealth.feature.NoHunger;
import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MobEffect.class)
public class MobEffectMixin {

	@ModifyConstant(method = "isDurationEffectTick", constant = @Constant(intValue = 25, ordinal = 0))
	public int onPoisonTickDamage(int constant) {
		if (NoHunger.isSlowerPoison()) return 80;
		return constant;
	}
}
