package insane96mcp.survivalreimagined.module.experience.enchanting;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class SREnchantingTableScreen extends AbstractContainerScreen<SREnchantingTableMenu> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");

    static final int BUTTON_X = 13;
    static final int BUTTON_Y = 38;
    static final int BUTTON_U = 0;
    static final int BUTTON_V = 166;
    static final int BUTTON_W = 26;
    static final int BUTTON_H = 18;

    static final int LIST_X = 52;
    static final int LIST_Y = 15;
    static final int ENCH_ENTRY_V = 184;
    static final int ENCH_ENTRY_H = 14;
    static final int LVL_BTN_W = 9;
    static final int LOWER_LVL_BTN_U = 0;
    static final int ENCH_DISPLAY_U = LVL_BTN_W;
    static final int ENCH_DISPLAY_W = 83;
    static final int ENCH_LVL_U = LVL_BTN_W + ENCH_DISPLAY_W;
    static final int ENCH_LVL_W = 14;
    static final int RISE_LVL_BTN_U = LVL_BTN_W + ENCH_DISPLAY_W + ENCH_LVL_W;

    private List<EnchantmentInstance> enchantments = new ArrayList<>();
    private List<EnchantmentEntry> enchantmentEntries = new ArrayList<>();
    private ItemStack lastStack;

    public SREnchantingTableScreen(SREnchantingTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.lastStack = ItemStack.EMPTY;
    }

    private void updatePossibleEnchantments() {
        ItemStack stack = this.menu.getSlot(0).getItem();
        if (ItemStack.isSameItem(stack, this.lastStack))
            return;
        this.lastStack = stack.copy();
        this.enchantments.clear();
        this.enchantmentEntries.clear();
        if (stack.isEmpty() || stack.isEnchanted())
            return;
        boolean isBook = stack.is(Items.BOOK);
        List<Enchantment> availableEnchantments = new ArrayList<>();
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (!enchantment.isTreasureOnly() && enchantment.isDiscoverable() && enchantment.canApplyAtEnchantingTable(stack) || (isBook && enchantment.isAllowedOnBooks())) {
                availableEnchantments.add(enchantment);
            }
        }
        availableEnchantments.forEach(enchantment -> this.enchantments.add(new EnchantmentInstance(enchantment, 0)));
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;
        for (int i = 0; i < this.enchantments.size(); i++) {
            EnchantmentInstance instance = this.enchantments.get(i);
            this.enchantmentEntries.add(new EnchantmentEntry(topLeftCornerX + LIST_X, topLeftCornerY + LIST_Y + (i * ENCH_ENTRY_H), instance.enchantment, instance.level));
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;

        double x = pMouseX - (double)(topLeftCornerX + BUTTON_X);
        double y = pMouseY - (double)(topLeftCornerY + BUTTON_Y);
        if (x >= 0.0D && y >= 0.0D && x < BUTTON_W && y < BUTTON_H && this.menu.clickMenuButton(this.minecraft.player, 0)) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 0);
            return true;
        }
        for (EnchantmentEntry enchantmentEntry : this.enchantmentEntries) {
            enchantmentEntry.levelDownBtn.mouseClicked(pMouseX, pMouseY, pButton);
            enchantmentEntry.levelUpBtn.mouseClicked(pMouseX, pMouseY, pButton);
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;
        int x = pMouseX - (topLeftCornerX + BUTTON_X);
        int y = pMouseY - (topLeftCornerY + BUTTON_Y);
        pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX, topLeftCornerY, 0, 0, this.imageWidth, this.imageHeight);
        int levelCost = this.menu.enchantCost.get();
        if ((this.minecraft.player.experienceLevel < levelCost && !this.minecraft.player.getAbilities().instabuild) || levelCost <= 0)
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + BUTTON_X, topLeftCornerY + BUTTON_Y, BUTTON_U + BUTTON_W, BUTTON_V, BUTTON_W, BUTTON_H);
        else if (x >= 0 && y >= 0 && x < BUTTON_W && y < BUTTON_H)
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + BUTTON_X, topLeftCornerY + BUTTON_Y, BUTTON_U + BUTTON_W * 2, BUTTON_V, BUTTON_W, BUTTON_H);
        else
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + BUTTON_X, topLeftCornerY + BUTTON_Y, BUTTON_U, BUTTON_V, BUTTON_W, BUTTON_H);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;

        updatePossibleEnchantments();
        /*for (int i = 0; i < 4; i++) {
            if (this.enchantments.size() <= i)
                break;
            EnchantmentInstance instance = this.enchantments.get(i);
            int x = pMouseX - (topLeftCornerX + LIST_X);
            int y = pMouseY - (topLeftCornerY + LIST_Y + (ENCH_ENTRY_H * i));
            int offset = 0;
            if (x >= 0 && y >= 0 && x < LVL_BTN_W + ENCH_DISPLAY_W + LVL_BTN_W && y < ENCH_ENTRY_H)
                offset = ENCH_ENTRY_H;
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + LIST_X, topLeftCornerY + LIST_Y + (ENCH_ENTRY_H * i), LOWER_LVL_BTN_U, LOWER_LVL_BTN_V + offset, LVL_BTN_W, ENCH_ENTRY_H);
            pGuiGraphics.blit(TEXTURE_LOCATION, topLeftCornerX + LIST_X + LVL_BTN_W + ENCH_DISPLAY_W, topLeftCornerY + LIST_Y + (ENCH_ENTRY_H * i), RISE_LVL_BTN_U, RISE_LVL_BTN_V + offset, LVL_BTN_W, ENCH_ENTRY_H);
        }*/
        this.enchantmentEntries.forEach(entry -> {
            entry.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        });

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private static class EnchantmentsList extends ScrollPanel {
        //List<>
        public EnchantmentsList(Minecraft client, int width, int height, int top, int left) {
            super(client, width, height, top, left);
        }

        @Override
        protected int getContentHeight() {
            return 0;
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {

        }

        @Override
        public NarrationPriority narrationPriority() {
            return null;
        }

        @Override
        protected int getScrollAmount() {
            return 1;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

        }
    }

    private class EnchantmentEntry {
        public LevelBtn levelDownBtn;
        public EnchantmentDisplay enchantmentDisplay;
        public LevelBtn levelUpBtn;

        public EnchantmentEntry(int pX, int pY, Enchantment enchantment, int lvl) {
            this.levelDownBtn = new LevelBtn(pX, pY, LevelBtn.Type.LOWER, this);
            this.enchantmentDisplay = new EnchantmentDisplay(pX + LVL_BTN_W, pY, enchantment, lvl);
            this.levelUpBtn = new LevelBtn(pX + RISE_LVL_BTN_U, pY, LevelBtn.Type.RISE, this);
        }

        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.levelDownBtn.render(guiGraphics, mouseX, mouseY, partialTick);
            this.enchantmentDisplay.render(guiGraphics, mouseX, mouseY, partialTick);
            this.levelUpBtn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private class LevelBtn extends AbstractButton {
        Type type;
        EnchantmentEntry enchantmentEntry;
        public LevelBtn(int pX, int pY, Type type, EnchantmentEntry enchantmentEntry) {
            super(pX, pY, LVL_BTN_W, ENCH_ENTRY_H, Component.empty());
            this.type = type;
            this.enchantmentEntry = enchantmentEntry;
        }

        @Override
        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            pGuiGraphics.blit(TEXTURE_LOCATION, this.getX(), this.getY(), this.type == Type.LOWER ? LOWER_LVL_BTN_U : RISE_LVL_BTN_U, ENCH_ENTRY_V + this.getYOffset(), this.width, this.height);
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = getFGColor();
            this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        private int getYOffset() {
            int i = 0;
            if (this.isHoveredOrFocused())
                i = 1;

            return i * this.height;
        }

        @Override
        public void onPress() {
            if (this.type == Type.LOWER)
                this.enchantmentEntry.enchantmentDisplay.lower();
            else
                this.enchantmentEntry.enchantmentDisplay.rise();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }

        enum Type {
            LOWER,
            RISE
        }
    }

    private static class EnchantmentDisplay extends AbstractWidget {

        Enchantment enchantment;
        int lvl;
        public EnchantmentDisplay(int pX, int pY, Enchantment enchantment, int lvl) {
            super(pX, pY, ENCH_DISPLAY_W, ENCH_ENTRY_H, Component.translatable(enchantment.getDescriptionId()));
            this.enchantment = enchantment;
            this.lvl = lvl;
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int x = pMouseX - this.getX();
            int y = pMouseY - this.getY();
            /*int offset = 0;
            if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight())
                offset = ENCH_ENTRY_H;*/
            pGuiGraphics.blit(TEXTURE_LOCATION, this.getX(), this.getY(), ENCH_DISPLAY_U, ENCH_ENTRY_V/* + offset*/, this.getWidth(), this.getHeight());
            pGuiGraphics.blit(TEXTURE_LOCATION, this.getX() + this.getWidth(), this.getY(), ENCH_DISPLAY_U + this.getWidth(), ENCH_ENTRY_V /*+ offset*/, this.getWidth(), this.getHeight());
            this.renderScrollingString(pGuiGraphics, Minecraft.getInstance().font, 2, 0xDDDDDD);
            Component lvlTxt = Component.empty();
            if (this.lvl > 0)
                lvlTxt = Component.translatable("enchantment.level." + this.lvl);
            pGuiGraphics.drawCenteredString(Minecraft.getInstance().font, lvlTxt, this.getX() + ENCH_DISPLAY_W + LVL_BTN_W / 2 + 2, this.getY() + 3, 0xDDDDDD);
        }

        public void rise() {
            if (this.lvl < this.enchantment.getMaxLevel())
                this.lvl++;
        }

        public void lower() {
            if (this.lvl > 0)
                this.lvl--;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

        }
    }
}
