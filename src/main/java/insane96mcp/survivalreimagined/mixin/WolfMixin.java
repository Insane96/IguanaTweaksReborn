package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements NeutralMob {

	protected WolfMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Inject(method = "getTailAngle", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void onTailAngle(CallbackInfoReturnable<Float> cir) {
		if (!this.isTame())
			return;
		cir.setReturnValue((0.55F - (20f - (this.getHealth() / this.getMaxHealth() * 20f)) * 0.02F) * (float)Math.PI);
	}
}
