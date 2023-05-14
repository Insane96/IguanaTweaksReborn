package insane96mcp.survivalreimagined.module.mining.inventory;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.crafting.AbstractMultiItemSmeltingRecipe;
import insane96mcp.survivalreimagined.module.mining.feature.MultiBlockFurnaces;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class MultiBlockSoulBlastFurnaceMenu extends AbstractMultiBlockFurnaceMenu {

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public MultiBlockSoulBlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container pBlastFurnaceContainer, ContainerData pBlastFurnaceData) {
        super(MultiBlockFurnaces.SOUL_BLAST_FURNACE_MENU_TYPE.get(), MultiBlockFurnaces.SOUL_BLASTING_RECIPE_TYPE.get(), SurvivalReimagined.MULTI_ITEM_RECIPE_BOOK_TYPE, pContainerId, pPlayerInventory, pBlastFurnaceContainer, pBlastFurnaceData);
        this.addSlot(new MultiBlockSoulBlastFurnaceFuelSlot(this, pBlastFurnaceContainer, FUEL_SLOT, 15, 13));
    }

    @Override
    protected boolean canSmelt(ItemStack pStack) {
        Optional<AbstractMultiItemSmeltingRecipe> recipe = this.level.getRecipeManager().getRecipeFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType, new SimpleContainer(pStack), this.level);
        return recipe.isPresent();
    }
}
