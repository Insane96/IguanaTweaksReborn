package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Inject(at = @At("HEAD"), method = "getUseDuration(Lnet/minecraft/item/ItemStack;)I", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!Modules.hungerHealth.foodConsuming.eatingSpeedBasedOffFood || !Modules.hungerHealth.foodConsuming.isEnabled())
			return;

		if (stack.getItem().isFood()) {
			callbackInfo.setReturnValue(Modules.hungerHealth.foodConsuming.getFoodConsumingTime(stack));
		}
	}
}
