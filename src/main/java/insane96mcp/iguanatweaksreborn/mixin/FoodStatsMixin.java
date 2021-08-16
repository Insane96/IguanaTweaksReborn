package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FoodStats.class, priority = 1)
public class FoodStatsMixin {
	@Inject(at = @At("HEAD"), method = "tick(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
	public void tick(PlayerEntity player, CallbackInfo callbackInfo) {
		FoodStats $this = (FoodStats) (Object) this;
		if (Modules.hungerHealth.healthRegen.tickFoodStats($this, player))
			callbackInfo.cancel();
	}
}
