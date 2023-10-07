package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.hungerhealth.healthregen.HealthRegen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FoodData.class, priority = 1)
public class FoodDataMixin {
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	public void tick(Player player, CallbackInfo callbackInfo) {
		if (HealthRegen.tickFoodStats((FoodData) (Object) this, player))
			callbackInfo.cancel();
	}
}
