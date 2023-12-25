package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.MultiBlockFurnaces;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting.AbstractMultiItemSmeltingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class MultiBlockSoulBlastFurnaceMenu extends AbstractMultiBlockFurnaceMenu {

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new ContainerOverMaxStack(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container pBlastFurnaceContainer, ContainerData pBlastFurnaceData) {
        super(MultiBlockFurnaces.SOUL_BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), IguanaTweaksReborn.MULTI_ITEM_SOUL_BLASTING_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory, pBlastFurnaceContainer, pBlastFurnaceData, List.of(
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
        return this.level.getRecipeManager().getAllRecipesFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType).stream().anyMatch(recipe -> recipe.hasIngredient(pStack, this.level));
    }

    public static int[] getIngredientSlots() {
        return new int[] {0, 1, 2, 3, 4, 5};
    }
}
