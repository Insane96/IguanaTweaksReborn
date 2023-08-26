package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnsorcellerScreen extends AbstractContainerScreen<EnsorcellerMenu> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/ensorceller.png");
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");

    int stepAnim = -1;

    public EnsorcellerScreen(EnsorcellerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void containerTick() {
        if (this.stepAnim == -1)
            this.stepAnim = this.menu.getSteps();
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
            double d1 = pMouseY - (double)(topLeftCornerY + 35 + 18 * buttonId);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 65 && d1 < 18 && this.menu.clickMenuButton(this.minecraft.player, buttonId)) {
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
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 102, topLeftCornerY + 68 - 4 * step, 0, 220, 42, 4);
            else
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 102, topLeftCornerY + 68 - 4 * step, 0, 224, 42, 4);
        }
        int x = pMouseX - (topLeftCornerX + 11);
        int y = pMouseY - (topLeftCornerY + 35);
        int levelCost = this.menu.rollCost.get();
        //Render Roll button
        if ((this.minecraft.player.experienceLevel < levelCost && !this.minecraft.player.getAbilities().instabuild) || this.menu.getSteps() == EnsorcellerMenu.MAX_STEPS) {
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 184, 65, 18);
            pGuiGraphics.drawString(this.font, "Roll", topLeftCornerX + 14, topLeftCornerY + 39, 0x8B8B8B, true);
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, topLeftCornerX + 11 + 65 - 17, topLeftCornerY + 36, 16 * (levelCost - 1), 239, 16, 16);
        }
        else if (x >= 0 && y >= 0 && x < 65 && y < 18) {
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 202, 65, 18);
            pGuiGraphics.drawString(this.font, "Roll", topLeftCornerX + 14, topLeftCornerY + 39, 0xFFFFFF, true);
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, topLeftCornerX + 11 + 65 - 17, topLeftCornerY + 36, 16 * (levelCost - 1), 223, 16, 16);
        }
        else {
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 35, 0, 166, 65, 18);
            pGuiGraphics.drawString(this.font, "Roll", topLeftCornerX + 14, topLeftCornerY + 39, 0xFFFFFF, true);
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, topLeftCornerX + 11 + 65 - 17, topLeftCornerY + 36, 16 * (levelCost - 1), 223, 16, 16);
        }

        //Render enchant button
        if (!this.menu.canEnchant()) {
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 53, 0, 184, 65, 18);
            pGuiGraphics.drawString(this.font, "Enchant", topLeftCornerX + 14, topLeftCornerY + 58, 0x8B8B8B, true);
        }
        else {
            y = pMouseY - (topLeftCornerY + 54);
            if (x >= 0 && y >= 0 && x < 65 && y < 18) {
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 53, 0, 202, 65, 18);
                pGuiGraphics.drawString(this.font, "Enchant", topLeftCornerX + 14, topLeftCornerY + 58, 0xFFFFFF, true);
            }
            else {
                pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + 11, topLeftCornerY + 53, 0, 166, 65, 18);
                pGuiGraphics.drawString(this.font, "Enchant", topLeftCornerX + 14, topLeftCornerY + 58, 0xFFFFFF, true);
            }
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
