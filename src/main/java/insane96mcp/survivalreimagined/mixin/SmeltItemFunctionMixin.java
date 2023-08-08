package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks.FoodDrinks;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SmeltItemFunction.class)
public class SmeltItemFunctionMixin {
    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private RecipeType<?> onRecipeType(RecipeType<?> recipeType) {
        if (FoodDrinks.noFurnaceFoodAndSmokerRecipe)
            return RecipeType.SMOKING;
        return recipeType;
    }
}
