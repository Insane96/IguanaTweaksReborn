package insane96mcp.survivalreimagined.module.experience.feature.enchanting;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnsorcellerScreen extends AbstractContainerScreen<EnsorcellerMenu> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/ensorceller.png");
    public EnsorcellerScreen(EnsorcellerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    int stepAnim = 0;

    @Override
    protected void containerTick() {
        if (this.stepAnim != this.menu.getSteps()) {
            if (this.stepAnim < this.menu.getSteps())
                this.stepAnim++;
            else if (this.stepAnim > this.menu.getSteps())
                this.stepAnim--;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;

        for (int buttonId = 0; buttonId < 2; ++buttonId) {
            double d0 = pMouseX - (double)(topLeftCornerX + 11);
            double d1 = pMouseY - (double)(topLeftCornerY + 35 + 19 * buttonId);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 45 && d1 < 19.0D && this.menu.clickMenuButton(this.minecraft.player, buttonId)) {
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, buttonId);
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX, topLeftCornerY, 0, 0, this.imageWidth, this.imageHeight);
        for (int step = 0; step < EnsorcellerMenu.MAX_STEPS; step++) {
            if (step + 1 <= stepAnim)
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 92, topLeftCornerY + 71 - 5 * step, 0, 223, 44, 5);
            else
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 92, topLeftCornerY + 71 - 5 * step, 0, 228, 44, 5);
        }
        int x = pMouseX - (topLeftCornerX + 11);
        int y = pMouseY - (topLeftCornerY + 35);
        //Render Roll button
        if ((this.minecraft.player.experienceLevel < 1 && !this.minecraft.player.getAbilities().instabuild) || this.menu.getSteps() == EnsorcellerMenu.MAX_STEPS)
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 185, 45, 19);
        else if (x >= 0 && y >= 0 && x < 45 && y < 19)
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 204, 45, 19);
        else
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 166, 45, 19);

        //Render enchant button
        if (!this.menu.canEnchant())
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 54, 45, 185, 45, 19);
        else {
            y = pMouseY - (topLeftCornerY + 54);
            if (x >= 0 && y >= 0 && x < 45 && y < 19)
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 54, 45, 204, 45, 19);
            else
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 54, 45, 166, 45, 19);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
