package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.fooddrinks.FoodDrinks;
import insane96mcp.iguanatweaksreborn.module.items.itemstats.ItemStats;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(at = @At("HEAD"), method = "getUseDuration", cancellable = true)
	public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
		if (!FoodDrinks.eatingSpeedBasedOffFood || !Feature.isEnabled(FoodDrinks.class))
			return;

		if (stack.getItem().getFoodProperties() != null)
			callbackInfo.setReturnValue(FoodDrinks.getFoodConsumingTime(stack));
	}

	@Inject(at = @At("RETURN"), method = "getUseAnimation", cancellable = true)
	public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> callbackInfo) {
		if (!Feature.isEnabled(FoodDrinks.class))
			return;

		if (stack.getItem() instanceof BowlFoodItem) {
			callbackInfo.setReturnValue(UseAnim.DRINK);
		}
	}

	@Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
	public void onGetBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (!Feature.isEnabled(ItemStats.class)
				|| !ItemStats.isBroken(stack))
			return;

		cir.setReturnValue(12);
	}

	@Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
	public void onGetBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (!Feature.isEnabled(ItemStats.class)
				|| !ItemStats.isBroken(stack))
			return;

		cir.setReturnValue(16711680);
	}
}
