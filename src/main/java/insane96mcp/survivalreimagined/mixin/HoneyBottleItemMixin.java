package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks.FoodDrinks;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleItemMixin {
    @Inject(at = @At("RETURN"), method = "getUseDuration", cancellable = true)
    public void getUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callbackInfo) {
        if (!FoodDrinks.fasterDrinkConsuming || !Feature.isEnabled(FoodDrinks.class))
            return;

        callbackInfo.setReturnValue(20);
    }
}
