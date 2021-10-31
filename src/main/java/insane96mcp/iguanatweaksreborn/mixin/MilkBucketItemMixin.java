package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {
	@Inject(at = @At("RETURN"), method = "getUseDuration(Lnet/minecraft/item/ItemStack;)I", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!Modules.hungerHealth.foodConsuming.fasterMilkConsuming || !Modules.hungerHealth.foodConsuming.isEnabled())
			return;

		callbackInfo.setReturnValue(20);
	}
}
