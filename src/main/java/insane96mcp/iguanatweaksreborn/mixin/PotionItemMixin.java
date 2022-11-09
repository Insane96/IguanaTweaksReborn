package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.Food;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {
	@Inject(at = @At("RETURN"), method = "getUseDuration", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!Food.fasterPotionConsuming || !Feature.isEnabled(Food.class))
			return;

		callbackInfo.setReturnValue(20);
	}
}
