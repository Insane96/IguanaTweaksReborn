package insane96mcp.iguanatweaksreborn.module.misc.beaconconduit;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.network.message.SetITRBeaconEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ITRBeaconScreen extends AbstractContainerScreen<ITRBeaconMenu> {
    static final ResourceLocation BEACON_LOCATION = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "textures/gui/container/beacon.png");
    public final List<BeaconButton> beaconButtons = Lists.newArrayList();
    @Nullable
    MobEffect effect;
    int maxAmplifier;
    int amplifier;

    public ITRBeaconScreen(final ITRBeaconMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 230;
        this.imageHeight = 219;
        pMenu.addSlotListener(new ContainerListener() {
            /**
             * Sends the contents of an inventory slot to the client-side Container. This doesn't have to match the actual
             * contents of that slot.
             */
            public void slotChanged(AbstractContainerMenu abstractContainerMenu, int slot, ItemStack stack) {
            }

            public void dataChanged(AbstractContainerMenu abstractContainerMenu, int id, int value) {
                ITRBeaconScreen.this.effect = pMenu.getEffect();
                ITRBeaconScreen.this.updateMaxAmplifier();
                ITRBeaconScreen.this.amplifier = pMenu.getAmplifier();
            }
        });
    }

    private <T extends AbstractWidget & BeaconButton> void addBeaconButton(T pBeaconButton) {
        this.addRenderableWidget(pBeaconButton);
        this.beaconButtons.add(pBeaconButton);
    }

    protected void init() {
        super.init();
        this.beaconButtons.clear();
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;
        for (int i = 0; i < 8; i++) {
            BeaconAmplifierButton amplifierButton = new BeaconAmplifierButton(topLeftCornerX + 17 + i * 25, topLeftCornerY + 78, i);
            amplifierButton.active = true;
            amplifierButton.visible = false;
            this.addBeaconButton(amplifierButton);
        }
        for (int i = 0; i < BeaconConduit.effects.size(); i++) {
            MobEffect mobeffect = BeaconConduit.effects.get(i).getEffect();
            int heightRequired = BeaconConduit.effects.get(i).getHeightRequired();
            BeaconPowerButton beaconEffectButton = new BeaconPowerButton(topLeftCornerX + 17 + (i % 8 * 25), topLeftCornerY + 15 + (i / 8 * 25), mobeffect, heightRequired);
            beaconEffectButton.active = true;
            if (Objects.equals(this.effect, mobeffect)) {
                beaconEffectButton.setSelected(true);
            }
            this.addBeaconButton(beaconEffectButton);
        }
    }

    private void updateMaxAmplifier() {
        this.maxAmplifier = this.menu.getMaxAmplifier(this.effect);
    }

    public void containerTick() {
        super.containerTick();
        this.updateButtons();
    }

    void updateButtons() {
        this.beaconButtons.forEach(BeaconButton::updateStatus);
    }

    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        /*pGuiGraphics.drawCenteredString(this.font, EFFECT_LABEL, 62, 10, 14737632);
        pGuiGraphics.drawCenteredString(this.font, AMPLIFIER_LABEL, 169, 10, 14737632);*/
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(BEACON_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int topLeftCornerX = (this.width - this.imageWidth) / 2;
        int topLeftCornerY = (this.height - this.imageHeight) / 2;
        pGuiGraphics.drawCenteredString(minecraft.font, Component.literal(StringUtil.formatTickDuration((int) (this.menu.getTimeLeft() / BeaconConduit.getEffectTimeScale(this.effect, this.amplifier)))), topLeftCornerX + 130, topLeftCornerY + 114, 16777215);
        /*if (this.menu.getTimeLeft() > 0) {
            pGuiGraphics.blit(BEACON_LOCATION, topLeftCornerX + 59, topLeftCornerY + 112, 89, 220, (int) (142 * ((float) this.menu.getTimeLeft() / ITRBeaconBlockEntity.MAX_TIME_LEFT)), 11);
        }*/
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    public interface BeaconButton {
        void updateStatus();
    }

    public class BeaconPowerButton extends BeaconScreenButton {
        public MobEffect effect;
        public int heightRequired;
        private TextureAtlasSprite sprite;

        public BeaconPowerButton(int pX, int pY, MobEffect pEffect, int heightRequired) {
            super(pX, pY);
            this.setEffect(pEffect);
            this.heightRequired = heightRequired;
        }

        protected void setEffect(MobEffect pEffect) {
            this.effect = pEffect;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(pEffect);
            this.setTooltip(Tooltip.create(this.createEffectDescription(pEffect), null));
        }

        protected MutableComponent createEffectDescription(MobEffect pEffect) {
            return Component.translatable(pEffect.getDescriptionId());
        }

        public void onPress() {
            if (!this.isSelected()) {
                ITRBeaconScreen.this.effect = this.effect;
                ITRBeaconScreen.this.amplifier = 0;
                ITRBeaconScreen.this.updateMaxAmplifier();
                SetITRBeaconEffects.updateServer(minecraft.player, ITRBeaconScreen.this.effect, amplifier);
                ITRBeaconScreen.this.updateButtons();
            }
        }

        protected void renderIcon(GuiGraphics pGuiGraphics) {
            pGuiGraphics.blit(this.getX() + 2, this.getY() + 2, 0, 18, 18, this.sprite);
        }

        public void updateStatus() {
            this.active = this.heightRequired <= ITRBeaconScreen.this.menu.getLayers();
            this.setSelected(this.effect == ITRBeaconScreen.this.effect);
        }

        protected MutableComponent createNarrationMessage() {
            return this.createEffectDescription(this.effect);
        }
    }

    public class BeaconAmplifierButton extends BeaconScreenButton {
        public int amplifier;

        public BeaconAmplifierButton(int pX, int pY, int amplifier) {
            super(pX, pY, 13, 13, 88);
            this.setAmplifier(amplifier);
        }

        protected void setAmplifier(int amplifier) {
            this.amplifier = amplifier;
        }

        protected MutableComponent createEffectDescription(MobEffect effect) {
            MutableComponent component = Component.translatable(effect.getDescriptionId()).append(" ");
            return component.append(getEffectAmplifier()).append(Component.literal(" (Time cost: %s)".formatted(BeaconConduit.getEffectTimeScale(ITRBeaconScreen.this.effect, this.amplifier))));
        }

        private Component getEffectAmplifier() {
            return this.amplifier == 0
                    ? Component.literal("I")
                    : Component.translatable("potion.potency." + this.amplifier);
        }

        public void onPress() {
            if (!this.isSelected()) {
                ITRBeaconScreen.this.amplifier = this.amplifier;
                SetITRBeaconEffects.updateServer(minecraft.player, ITRBeaconScreen.this.effect, this.amplifier);
                ITRBeaconScreen.this.updateButtons();
            }
        }

        public void updateStatus() {
            this.visible = this.amplifier <= ITRBeaconScreen.this.maxAmplifier && ITRBeaconScreen.this.effect != null;
            this.active = this.amplifier + 1 <= ITRBeaconScreen.this.menu.getLayers();
            this.setSelected(this.amplifier == ITRBeaconScreen.this.amplifier);
            if (ITRBeaconScreen.this.effect != null)
                this.setTooltip(Tooltip.create(this.createEffectDescription(ITRBeaconScreen.this.effect), null));
        }

        @Override
        protected void renderIcon(GuiGraphics pGuiGraphics) {
            pGuiGraphics.drawCenteredString(minecraft.font, this.getEffectAmplifier(), this.getX() + 7, this.getY() + 3, 16777215);
        }
    }

    abstract static class BeaconScreenButton extends AbstractButton implements BeaconButton {
        private boolean selected;
        int u, v;

        protected BeaconScreenButton(int pX, int pY) {
            this(pX, pY, 22, 22, 0);
        }

        protected BeaconScreenButton(int pX, int pY, int width, int height, int u) {
            super(pX, pY, width, height, CommonComponents.EMPTY);
            this.u = u;
        }

        public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int i = 219;
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.selected) {
                j += this.width;
            } else if (this.isHoveredOrFocused()) {
                j += this.width * 3;
            }

            pGuiGraphics.blit(BEACON_LOCATION, this.getX(), this.getY(), j + u, 219, this.width, this.height);
            this.renderIcon(pGuiGraphics);
        }

        protected abstract void renderIcon(GuiGraphics pGuiGraphics);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean pSelected) {
            this.selected = pSelected;
        }

        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
            this.defaultButtonNarrationText(pNarrationElementOutput);
        }
    }
}