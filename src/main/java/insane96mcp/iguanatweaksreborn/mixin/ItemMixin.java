package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.feature.Food;
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
		if (!Food.eatingSpeedBasedOffFood || !Feature.isEnabled(Food.class))
			return;

		if (stack.getItem().isEdible()) {
			callbackInfo.setReturnValue(Food.getFoodConsumingTime(stack));
		}
	}

	@Inject(at = @At("RETURN"), method = "getUseAnimation", cancellable = true)
	public void getUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> callbackInfo) {
		if (!Feature.isEnabled(Food.class))
			return;

		if (stack.getItem() instanceof BowlFoodItem) {
			callbackInfo.setReturnValue(UseAnim.DRINK);
		}
	}
}
