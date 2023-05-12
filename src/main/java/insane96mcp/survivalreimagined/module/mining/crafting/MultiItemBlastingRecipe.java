package insane96mcp.survivalreimagined.module.mining.crafting;

import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

public class MultiItemBlastingRecipe extends AbstractMultiItemSmeltingRecipe {
    public MultiItemBlastingRecipe(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, NonNullList<Ingredient> ingredients, ItemStack pResult, float doubleOutputChance, float pExperience, int pCookingTime) {
        super(MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), pId, pGroup, pCategory, ingredients, pResult, doubleOutputChance, pExperience, pCookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.BLAST_FURNACE);
    }
}
