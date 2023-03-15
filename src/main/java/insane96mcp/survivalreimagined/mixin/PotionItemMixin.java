package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.client.feature.Misc;
import insane96mcp.survivalreimagined.module.hungerhealth.feature.FoodDrinks;
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
		if (!FoodDrinks.fasterDrinkConsuming || !Feature.isEnabled(FoodDrinks.class))
			return;

		callbackInfo.setReturnValue(20);
	}

	@Inject(at = @At("RETURN"), method = "isFoil", cancellable = true)
	public void isFoil(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
		if (!Misc.shouldRemovePotionEnchantGlint())
			return;

		callbackInfo.setReturnValue(false);
	}
}
