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
import java.util.Optional;

public class MultiBlockSoulBlastFurnaceMenu extends AbstractMultiBlockFurnaceMenu {

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container pBlastFurnaceContainer, ContainerData pBlastFurnaceData) {
        super(MultiBlockFurnaces.SOUL_BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), SurvivalReimagined.MULTI_ITEM_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory, pBlastFurnaceContainer, pBlastFurnaceData, List.of(
                new Slot(pBlastFurnaceContainer, 0, 43, 26),
                new Slot(pBlastFurnaceContainer, 1, 61, 26),
                new Slot(pBlastFurnaceContainer, 2, 79, 26),
                new Slot(pBlastFurnaceContainer, 3, 43, 44),
                new Slot(pBlastFurnaceContainer, 4, 61, 44),
                new Slot(pBlastFurnaceContainer, 5, 79, 44)
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
        Optional<AbstractMultiItemSmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType, new SimpleContainer(pStack), this.level);
        return recipe.isPresent();
    }

    public static int[] getIngredientSlots() {
        return new int[] {0, 1, 2, 3, 4, 5};
    }
}
