package insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.crafting;

import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MultiItemSoulBlastingRecipe extends AbstractMultiItemSmeltingRecipe {
    public MultiItemSoulBlastingRecipe(ResourceLocation pId, String pGroup, SRBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float outputIncrease, float pExperience, int pCookingTime, Recycle recycle) {
        super(MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), pId, pGroup, pCategory, 6, ingredients, pResult, outputIncrease, pExperience, pCookingTime, recycle);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get());
    }

    @Override
    public boolean matches(Container container, Level pLevel) {
        return super.matches(container, pLevel);
    }

    @Override
    int[] getIngredientSlots() {
        return new int[] {0, 1, 2, 3, 4, 5};
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MultiBlockFurnaces.SOUL_BLASTING_RECIPE_SERIALIZER.get();
    }
}
