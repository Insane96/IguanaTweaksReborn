package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {
	@Inject(at = @At("RETURN"), method = "getUseDuration(Lnet/minecraft/item/ItemStack;)I", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!Modules.hungerHealth.foodConsuming.fasterPotionConsuming || !Modules.hungerHealth.foodConsuming.isEnabled())
			return;

		callbackInfo.setReturnValue(20);
	}
}
