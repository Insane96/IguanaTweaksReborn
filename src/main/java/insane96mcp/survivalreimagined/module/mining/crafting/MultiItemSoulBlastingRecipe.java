package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public class MultiItemSoulBlastingRecipe extends AbstractMultiItemSmeltingRecipe {
    public MultiItemSoulBlastingRecipe(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float doubleOutputChance, float pExperience, int pCookingTime, Recycle recycle) {
        super(MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), pId, pGroup, pCategory, ingredients, pResult, doubleOutputChance, pExperience, pCookingTime, recycle);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(MultiBlockFurnaces.SOUL_BLAST_FURNACE.item().get());
    }
}
