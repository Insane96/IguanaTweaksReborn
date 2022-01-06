package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.Modules;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(at = @At("HEAD"), method = "getUseDuration", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!Modules.hungerHealth.foodConsuming.eatingSpeedBasedOffFood || !Modules.hungerHealth.foodConsuming.isEnabled())
			return;

		if (stack.getItem().isEdible()) {
			callbackInfo.setReturnValue(Modules.hungerHealth.foodConsuming.getFoodConsumingTime(stack));
		}
	}
}
