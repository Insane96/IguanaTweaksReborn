package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import insane96mcp.survivalreimagined.setup.client.SRBookCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;

public class MultiItemBlastingRecipe extends AbstractMultiItemSmeltingRecipe {
    public MultiItemBlastingRecipe(ResourceLocation pId, String pGroup, SRBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float doubleOutputChance, float pExperience, int pCookingTime, Recycle recycle) {
        super(MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), pId, pGroup, pCategory, 4, ingredients, pResult, doubleOutputChance, pExperience, pCookingTime, recycle);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }

    @Override
    int[] getIngredientSlots() {
        return new int[] {0, 1, 2, 3};
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MultiBlockFurnaces.BLASTING_RECIPE_SERIALIZER.get();
    }
}
