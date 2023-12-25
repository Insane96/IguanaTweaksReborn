package insane96mcp.iguanatweaksreborn.mixin;

import com.mojang.logging.LogUtils;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.fooddrinks.FoodDrinks;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SmeltItemFunction.class)
public class SmeltItemFunctionMixin {

    @Final
    @Shadow
    private static final Logger LOGGER = LogUtils.getLogger();

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void onRecipeRun(ItemStack pStack, LootContext pContext, CallbackInfoReturnable<ItemStack> cir) {
        if (FoodDrinks.noFurnaceFoodAndSmokerRecipe) {
            if (pStack.isEmpty()) {
                cir.setReturnValue(pStack);
            } else {
                Optional<CampfireCookingRecipe> optional = pContext.getLevel().getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(pStack), pContext.getLevel());
                if (optional.isPresent()) {
                    ItemStack itemstack = optional.get().getResultItem(pContext.getLevel().registryAccess());
                    if (!itemstack.isEmpty()) {
                        cir.setReturnValue(itemstack.copyWithCount(pStack.getCount() * itemstack.getCount())); // Forge: Support smelting returning multiple
                    }
                }

                LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)pStack);
                cir.setReturnValue(pStack);
            }
        }

    }
}
