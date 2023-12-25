package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory;

import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting.AbstractMultiItemSmeltingRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class AbstractMultiBlockFurnaceMenu extends RecipeBookMenu<Container> {
    public static final int[] INVENTORY_SLOTS = new int[] {0, 1, 2, 3, 4, 5};
    public static final int FUEL_SLOT = 6;
    public static final int RESULT_SLOT = FUEL_SLOT + 1;
    public static final int SLOT_COUNT = 8;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = RESULT_SLOT + 1;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    final RecipeType<? extends AbstractMultiItemSmeltingRecipe> recipeType;
    private final RecipeBookType recipeBookType;

    protected AbstractMultiBlockFurnaceMenu(MenuType<?> pMenuType, RecipeType<? extends AbstractMultiItemSmeltingRecipe> pRecipeType, RecipeBookType pRecipeBookType, int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData, List<Slot> perMenuSlots) {
        super(pMenuType, pContainerId);
        this.recipeType = pRecipeType;
        this.recipeBookType = pRecipeBookType;
        checkContainerSize(pContainer, SLOT_COUNT);
        checkContainerDataCount(pData, DATA_COUNT);
        this.container = pContainer;
        this.data = pData;
        this.level = pPlayerInventory.player.level();

        perMenuSlots.forEach(AbstractMultiBlockFurnaceMenu.this::addSlot);
        if (this instanceof MultiBlockSoulBlastFurnaceMenu)
            this.addSlot(new MultiBlockSoulBlastFurnaceFuelSlot(this, pContainer, FUEL_SLOT, 15, 13));
        else
            this.addSlot(new MultiBlockFurnaceFuelSlot(this, pContainer, FUEL_SLOT, 15, 13));
        this.addSlot(new MultiBlockFurnaceResultSlot(pPlayerInventory.player, pContainer, RESULT_SLOT, 142, 35));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(pData);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents pItemHelper) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)this.container).fillStackedContents(pItemHelper);
        }
    }

    @Override
    public abstract void clearCraftingContent();

    @Override
    public boolean recipeMatches(Recipe<? super Container> pRecipe) {
        return pRecipe.matches(this.container, this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return 4;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return SLOT_COUNT;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return this.recipeBookType;
    }

    @Override
    public boolean shouldMoveToInventory(int pSlotIndex) {
        return pSlotIndex != FUEL_SLOT;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pSlot);
        if (slot.hasItem()) {
            ItemStack itemStackInSlot = slot.getItem();
            itemstack = itemStackInSlot.copy();
            if (pSlot == RESULT_SLOT) {
                if (!this.moveItemStackTo(itemStackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStackInSlot, itemstack);
            }
            //If inventory slots
            else if (pSlot > FUEL_SLOT) {
                if (this.canSmelt(itemStackInSlot)) {
                    if (!this.moveItemStackTo(itemStackInSlot, 0, FUEL_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (this.isFuel(itemStackInSlot)) {
                    if (!this.moveItemStackTo(itemStackInSlot, FUEL_SLOT, RESULT_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (pSlot >= INV_SLOT_START && pSlot < INV_SLOT_END) {
                    if (!this.moveItemStackTo(itemStackInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (pSlot >= USE_ROW_SLOT_START && pSlot < USE_ROW_SLOT_END && !this.moveItemStackTo(itemStackInSlot, INV_SLOT_START, INV_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.moveItemStackTo(itemStackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if (itemStackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemStackInSlot);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    protected boolean isFuel(ItemStack pStack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(pStack, this.recipeType) > 0;
    }

    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getAllRecipesFor((RecipeType<AbstractMultiItemSmeltingRecipe>)this.recipeType).stream().anyMatch(recipe -> recipe.hasIngredient(pStack, this.level));
    }

    public int getBurnProgress() {
        int i = this.data.get(2);
        int j = this.data.get(3);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    public int getLitProgress() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 200;
        }

        return this.data.get(0) * 13 / i;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }
}
