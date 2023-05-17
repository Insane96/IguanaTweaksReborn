package insane96mcp.survivalreimagined.module.mining.inventory;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.crafting.AbstractMultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class MultiBlockBlastFurnaceMenu extends AbstractMultiBlockFurnaceMenu {

    public MultiBlockBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public MultiBlockBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container pBlastFurnaceContainer, ContainerData pBlastFurnaceData) {
        super(MultiBlockFurnaces.BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.BLASTING_RECIPE_TYPE.get(), SurvivalReimagined.MULTI_ITEM_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory, pBlastFurnaceContainer, pBlastFurnaceData, List.of(
                new Slot(pBlastFurnaceContainer, 0, 51, 26),
                new Slot(pBlastFurnaceContainer, 1, 69, 26),
                new Slot(pBlastFurnaceContainer, 2, 61, 44),
                new Slot(pBlastFurnaceContainer, 3, 79, 44),
                new DisabledSlot(pBlastFurnaceContainer, 4, -16, -16),
                new DisabledSlot(pBlastFurnaceContainer, 5, -16, -16)
        ));
    }

    @Override
    public void clearCraftingContent() {
        for (int slot : getIngredientSlots()) {
            this.getSlot(slot).set(ItemStack.EMPTY);
        }
        this.getSlot(RESULT_SLOT).set(ItemStack.EMPTY);
    }

    @Override
    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getAllRecipesFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType).stream().anyMatch(recipe -> recipe.hasIngredient(pStack, this.level));
    }

    public static int[] getIngredientSlots() {
        return new int[] {0, 1, 2, 3};
    }
}
