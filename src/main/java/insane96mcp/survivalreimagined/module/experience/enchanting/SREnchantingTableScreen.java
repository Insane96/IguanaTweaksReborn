package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SREnchantingTableScreen extends AbstractContainerScreen<SREnchantingTableMenu> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(SurvivalReimagined.MOD_ID, "textures/gui/container/ensorceller.png");
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");

    public SREnchantingTableScreen(SREnchantingTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
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

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    /*private class ScrollingList extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
        private final List<EnchantmentListEntry> children = Lists.newArrayList();
        private final int posX;
        private final int posY;
        private final int itemWidth;
        private final int itemHeight;
        private final int length;
        private int scrollPosition;

        public ScrollingList(int posX, int posY, int itemWidth, int itemHeight, int length) {
            this.posX = posX;
            this.posY = posY;
            this.itemWidth = itemWidth;
            this.itemHeight = itemHeight;
            this.length = length;
        }

        public void scrollTo(float pos) {
            if (pos < 0.0F || pos > 1.0F) throw new IllegalArgumentException("pos must be of interval 0 to 1");
            if (this.canScroll()) {
                // important to round instead of int cast
                this.scrollPosition = Math.round((this.getItemCount() - this.length) * pos);
            } else {
                this.scrollPosition = 0;
            }
        }

        public boolean canScroll() {
            return this.getItemCount() > this.length;
        }

        protected final void clearEntries() {
            this.children.clear();
        }

        protected void addEntry(EnchantmentListEntry pEntry) {
            this.children.add(pEntry);
            pEntry.setList(this);
            this.markOthersIncompatible();
        }

        protected int getItemCount() {
            return this.children.size();
        }

        public void markOthersIncompatible() {
            final List<EnchantmentListEntry> activeEnchants = this.children.stream()
                    .filter(EnchantmentListEntry::isActive)
                    .toList();
            for (EnchantmentListEntry entry : this.children) {
                if (!entry.isActive()) {
                    entry.markIncompatible(activeEnchants.stream()
                            .filter(e -> e.isIncompatibleWith(entry))
                            .collect(Collectors.toSet()));
                }
            }
        }

        @Nullable
        protected final EnchantmentListEntry getEntryAtPosition(double mouseX, double mouseY) {
            if (this.isMouseOver(mouseX, mouseY)) {
                final int index = this.scrollPosition + (int) ((mouseY - this.posY) / this.itemHeight);
                return index < this.children.size() ? this.children.get(index) : null;
            }
            return null;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= this.posX && mouseX < this.posX + this.itemWidth && mouseY >= this.posY && mouseY < this.posY + this.itemHeight * this.length;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            } else {
                EnchantmentListEntry entry = this.getEntryAtPosition(mouseX, mouseY);
                if (entry != null) {
                    if (entry.mouseClicked(mouseX, mouseY, button)) {
                        this.setFocused(entry);
                        this.setDragging(true);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            if (this.getFocused() != null) {
                this.getFocused().mouseReleased(pMouseX, pMouseY, pButton);
            }
            return false;
        }

        @Override
        public List<EnchantmentListEntry> children() {
            return this.children;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            for (int i = 0; i < Math.min(this.length, this.getItemCount()); i++) {
                this.children.get(this.scrollPosition + i).render(guiGraphics, this.posX, this.posY + this.itemHeight * i, this.itemWidth, this.itemHeight, mouseX, mouseY, partialTick);
            }
        }

        @Override
        public NarrationPriority narrationPriority() {
            // TODO proper implementation
            return NarratableEntry.NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            // TODO proper implementation
        }
    }

    private class EnchantmentListEntry implements ContainerEventHandler {
        private static final Component UNKNOWN_ENCHANT_COMPONENT = Component.translatable("gui.enchantinginfuser.tooltip.unknown_enchantment").withStyle(ChatFormatting.GRAY);
        private static final Component LOW_POWER1_COMPONENT = Component.translatable("gui.enchantinginfuser.tooltip.lowPower1").withStyle(ChatFormatting.GRAY);
        private static final Component LOW_POWER2_COMPONENT = Component.translatable("gui.enchantinginfuser.tooltip.lowPower2").withStyle(ChatFormatting.GRAY);

        private final Enchantment enchantment;
        private final int maxLevel;
        private final int requiredPower;
        private final Button decrButton;
        private final Button incrButton;
        private int level;
        private ScrollingList list;
        @Nullable
        private GuiEventListener focused;
        private boolean dragging;
        private Set<Enchantment> incompatible = Sets.newHashSet();

        public EnchantmentListEntry(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            final Pair<OptionalInt, Integer> maxLevelResult = EnsorcellerScreen.this.menu.getMaxLevel(enchantment);
            this.maxLevel = maxLevelResult.getSecond();
            this.requiredPower = maxLevelResult.getFirst().orElse(-1);
            this.level = level;
            this.decrButton = new IconButton(0, 0, 18, 18, 220, 0, INFUSER_LOCATION, button -> {
                do {
                    final int newLevel = InfuserScreen.this.menu.clickEnchantmentLevelButton(InfuserScreen.this.minecraft.player, this.enchantment, false);
                    if (newLevel == -1) return;
                    this.level = newLevel;
                    EnchantingInfuser.NETWORK.sendToServer(new C2SAddEnchantLevelMessage(InfuserScreen.this.menu.containerId, this.enchantment, false));
                    this.updateButtons();
                    this.list.markOthersIncompatible();
                } while (button.active && button.visible && Screen.hasShiftDown());
            }) {

                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    if (this.active && Screen.hasShiftDown()) {
                        RenderSystem.enableDepthTest();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                        int index = !this.active ? 0 : this.isHoveredOrFocused() ? 2 : 1;
                        guiGraphics.blit(this.resourceLocation, this.getX() + 2, this.getY(), this.xTexStart, this.yTexStart + index * this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                        guiGraphics.blit(this.resourceLocation, this.getX() - 4, this.getY(), this.xTexStart, this.yTexStart + index * this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                        this.renderTooltip();
                    } else {
                        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
                        this.renderTooltip();
                    }
                }

                private void renderTooltip() {
                    if (this.isHoveredOrFocused()) {
                        if (EnchantmentListEntry.this.level - 1 >= EnchantmentListEntry.this.maxLevel && !EnchantmentListEntry.this.isObfuscated()) {
                            InfuserScreen.this.setTooltipForNextRenderPass(EnchantmentListEntry.this.getLowPowerComponent(LOW_POWER2_COMPONENT));
                            InfuserScreen.this.insufficientPower = true;
                        }
                    }
                }
            };
            this.incrButton = new IconButton(0, 0, 18, 18, 238, 0, INFUSER_LOCATION, button -> {
                do {
                    final int newLevel = InfuserScreen.this.menu.clickEnchantmentLevelButton(InfuserScreen.this.minecraft.player, this.enchantment, true);
                    if (newLevel == -1) return;
                    this.level = newLevel;
                    EnchantingInfuser.NETWORK.sendToServer(new C2SAddEnchantLevelMessage(InfuserScreen.this.menu.containerId, this.enchantment, true));
                    this.updateButtons();
                    this.list.markOthersIncompatible();
                } while (button.active && button.visible && Screen.hasShiftDown());
            }) {

                @Override
                public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
                    if (this.active && Screen.hasShiftDown()) {
                        RenderSystem.enableDepthTest();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
                        int index = !this.active ? 0 : this.isHoveredOrFocused() ? 2 : 1;
                        guiGraphics.blit(this.resourceLocation, this.getX() - 2, this.getY(), this.xTexStart, this.yTexStart + index * this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                        guiGraphics.blit(this.resourceLocation, this.getX() + 4, this.getY(), this.xTexStart, this.yTexStart + index * this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
                        this.renderTooltip();
                    } else {
                        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
                        this.renderTooltip();
                    }
                }

                private void renderTooltip() {
                    if (this.isHoveredOrFocused()) {
                        if (EnchantmentListEntry.this.level >= EnchantmentListEntry.this.maxLevel && !EnchantmentListEntry.this.isObfuscated()) {
                            InfuserScreen.this.setTooltipForNextRenderPass(EnchantmentListEntry.this.getLowPowerComponent(LOW_POWER1_COMPONENT));
                            InfuserScreen.this.insufficientPower = true;
                        }
                    }
                }
            };
            this.updateButtons();
        }
*/

    }
