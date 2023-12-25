package insane96mcp.survivalreimagined.module.mining.forging;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ForgeMenu extends RecipeBookMenu<Container> {
    public static final int INGREDIENT_SLOT = 0;
    public static final int GEAR_SLOT = INGREDIENT_SLOT + 1;
    public static final int RESULT_SLOT = GEAR_SLOT + 1;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_COUNT = 2;
    private static final int INV_SLOT_START = RESULT_SLOT + 1;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    final RecipeType<? extends ForgeRecipe> recipeType;
    private final RecipeBookType recipeBookType;

    public ForgeMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public ForgeMenu(int pContainerId, Inventory pPlayerInventory, Container pContainer, ContainerData pData) {
        super(Forging.FORGE_MENU_TYPE.get(), pContainerId);
        this.recipeType = Forging.FORGE_RECIPE_TYPE.get();
        this.recipeBookType = SurvivalReimagined.FORGING_RECIPE_BOOK_TYPE;
        checkContainerSize(pContainer, SLOT_COUNT);
        checkContainerDataCount(pData, DATA_COUNT);
        this.container = pContainer;
        this.data = pData;
        this.level = pPlayerInventory.player.level();

        this.addSlot(new Slot(pContainer, INGREDIENT_SLOT, 56, 17));
        this.addSlot(new Slot(pContainer, GEAR_SLOT, 56, 53));
        this.addSlot(new ForgeResultSlot(pPlayerInventory.player, pContainer, RESULT_SLOT, 116, 35));

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
    public void clearCraftingContent() {
        this.getSlot(ForgeMenu.INGREDIENT_SLOT).set(ItemStack.EMPTY);
        this.getSlot(ForgeMenu.GEAR_SLOT).set(ItemStack.EMPTY);
        this.getSlot(ForgeMenu.RESULT_SLOT).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(Recipe<? super Container> pRecipe) {
        return pRecipe.matches(this.container, this.level);
    }

    @Override
    public void handlePlacement(boolean pPlaceAll, Recipe<?> pRecipe, ServerPlayer pPlayer) {
        new ForgePlaceRecipe(this).recipeClicked(pPlayer, (Recipe<Container>) pRecipe, pPlaceAll);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return 1;
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
        return true;
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
            else if (pSlot > RESULT_SLOT) {
                if (this.isIngredient(itemStackInSlot)) {
                    if (!this.moveItemStackTo(itemStackInSlot, INGREDIENT_SLOT, GEAR_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (this.canForge(itemStackInSlot)) {
                    if (!this.moveItemStackTo(itemStackInSlot, GEAR_SLOT, RESULT_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (pSlot < INV_SLOT_END) {
                    if (!this.moveItemStackTo(itemStackInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (pSlot < USE_ROW_SLOT_END && !this.moveItemStackTo(itemStackInSlot, INV_SLOT_START, INV_SLOT_END, false)) {
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

    protected boolean canForge(ItemStack pStack) {
        return this.level.getRecipeManager().getAllRecipesFor((RecipeType<ForgeRecipe>)this.recipeType).stream().anyMatch(recipe -> recipe.getGear().test(pStack));
    }

    protected boolean isIngredient(ItemStack pStack) {
        return this.level.getRecipeManager().getAllRecipesFor((RecipeType<ForgeRecipe>)this.recipeType).stream().anyMatch(recipe -> recipe.getIngredient().test(pStack));
    }

    public int getForgeProgress() {
        int smashesRequired = this.data.get(ForgeBlockEntity.DATA_SMASHES_REQUIRED);
        if (smashesRequired == 0) {
            smashesRequired = 5;
        }

        return this.data.get(ForgeBlockEntity.DATA_SMASHES) * 24 / smashesRequired;
    }
}
