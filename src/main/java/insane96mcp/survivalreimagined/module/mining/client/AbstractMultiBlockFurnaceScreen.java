package insane96mcp.survivalreimagined.module.mining.client;

import insane96mcp.survivalreimagined.module.mining.inventory.AbstractMultiBlockFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public abstract class AbstractMultiBlockFurnaceScreen<T extends AbstractMultiBlockFurnaceMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    public final AbstractMultiBlockFurnaceRecipeBookComponent recipeBookComponent;
    private boolean widthTooNarrow;
    private final ResourceLocation texture;

    public AbstractMultiBlockFurnaceScreen(T pMenu, AbstractMultiBlockFurnaceRecipeBookComponent pRecipeBookComponent, Inventory pPlayerInventory, Component pTitle, ResourceLocation pTexture) {
        super(pMenu, pPlayerInventory, pTitle);
        this.recipeBookComponent = pRecipeBookComponent;
        this.texture = pTexture;
    }

    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.addRenderableWidget(new ImageButton(this.leftPos + 13, this.height / 2 - 32, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 13, this.height / 2 - 32);
        }));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
            this.recipeBookComponent.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        } else {
            this.recipeBookComponent.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            this.recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, true, pPartialTick);
        }

        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
        this.recipeBookComponent.renderTooltip(guiGraphics, this.leftPos, this.topPos, pMouseX, pMouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pX, int pY) {
        int leftPos = this.leftPos;
        int topPos = this.topPos;
        guiGraphics.blit(this.texture, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            int k = this.menu.getLitProgress();
            guiGraphics.blit(this.texture, leftPos + 16, topPos + 35 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.menu.getBurnProgress();
        guiGraphics.blit(this.texture, leftPos + 105, topPos + 34, 176, 14, l + 1, 16);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.recipeBookComponent.mouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(pMouseX, pMouseY, pButton);
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
        this.recipeBookComponent.slotClicked(pSlot);
    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return this.recipeBookComponent.keyPressed(pKeyCode, pScanCode, pModifiers) ? false : super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
        boolean flag = pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside(pMouseX, pMouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, pMouseButton) && flag;
    }

    public boolean charTyped(char pCodePoint, int pModifiers) {
        return this.recipeBookComponent.charTyped(pCodePoint, pModifiers) ? true : super.charTyped(pCodePoint, pModifiers);
    }

    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}