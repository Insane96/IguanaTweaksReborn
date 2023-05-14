package insane96mcp.survivalreimagined.module.mining.inventory;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.crafting.AbstractMultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class MultiBlockBlastFurnaceMenu extends AbstractMultiBlockFurnaceMenu {

    public MultiBlockBlastFurnaceMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractMultiItemSmeltingRecipe> pRecipeType, RecipeBookType pRecipeBookType, int pContainerId, Inventory pPlayerInventory) {
        super(pMenuType, pRecipeType, pRecipeBookType, pContainerId, pPlayerInventory);
    }

    public MultiBlockBlastFurnaceMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractMultiItemSmeltingRecipe> pRecipeType, RecipeBookType pRecipeBookType, int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData) {
        super(pMenuType, pRecipeType, pRecipeBookType, pContainerId, pPlayerInventory, pContainer, pData);
    }

    public MultiBlockBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        super(MultiBlockFurnaces.BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), SurvivalReimagined.MULTI_ITEM_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory);
    }

    public MultiBlockBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container pBlastFurnaceContainer, ContainerData pBlastFurnaceData) {
        super(MultiBlockFurnaces.BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), SurvivalReimagined.MULTI_ITEM_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory, pBlastFurnaceContainer, pBlastFurnaceData);
    }

    @Override
    protected boolean canSmelt(ItemStack pStack) {
        Optional<AbstractMultiItemSmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType, new SimpleContainer(pStack), this.level);
        return recipe.isPresent();
    }
}
