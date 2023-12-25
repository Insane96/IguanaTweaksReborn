package insane96mcp.survivalreimagined.module.mining.forging;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Iterator;

public class ForgePlaceRecipe extends ServerPlaceRecipe<Container> {
    public ForgePlaceRecipe(RecipeBookMenu<Container> pMenu) {
        super(pMenu);
    }

    @Override
    public void placeRecipe(int pWidth, int pHeight, int pOutputSlot, Recipe<?> pRecipe, Iterator<Integer> pIngredients, int pMaxAmount) {
        this.addItemToSlot(pIngredients, 0, ((ForgeRecipe)pRecipe).ingredientAmount, 0, 0);
        this.addItemToSlot(pIngredients, 1, 64, 0, 0);
    }
}
