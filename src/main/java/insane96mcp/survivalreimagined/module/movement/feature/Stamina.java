package insane96mcp.survivalreimagined.module.movement.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.event.PlayerSprintEvent;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.NetworkHandler;
import insane96mcp.survivalreimagined.network.message.MessageStaminaSync;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import org.lwjgl.opengl.GL11;

@Label(name = "Stamina", description = "Stamina to let the player run and do stuff.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class Stamina extends Feature {

    public static final String STAMINA = SurvivalReimagined.RESOURCE_PREFIX + "stamina";
    public static final String STAMINA_LOCKED = SurvivalReimagined.RESOURCE_PREFIX + "stamina_locked";

    @Config(min = 0)
    @Label(name = "Stamina per half heart", description = "How much stamina the player has per half heart. Each 1 stamina is 1 tick of running")
    public static Integer staminaPerHalfHeart = 10;

    @Config(min = 0)
    @Label(name = "Stamina consumed on jump", description = "How much stamina the player consumes on each jump")
    public static Integer staminaConsumedOnJump = 0;

    public Stamina(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    /**
     * Each half heart accounts for 1/2 a second sprinting
     */
    public static int getMaxStamina(Player player) {
        return Mth.ceil(player.getHealth() * staminaPerHalfHeart);
    }

    public static int getStamina(Player player) {
        return player.getPersistentData().getInt(STAMINA);
    }

    public static boolean isStaminaLocked(Player player) {
        return player.getPersistentData().getBoolean(STAMINA_LOCKED);
    }

    public static boolean canSprint(Player player) {
        return !isStaminaLocked(player) && getStamina(player) > 0 && !player.getAbilities().instabuild;
    }

    public static void setStamina(Player player, int stamina) {
        player.getPersistentData().putInt(STAMINA, Mth.clamp(stamina, 0, getMaxStamina(player)));
    }

    public static void consumeStamina(Player player) {
        setStamina(player, getStamina(player) - 1);
    }

    public static void consumeStamina(Player player, int amount) {
        setStamina(player, getStamina(player) - amount);
    }

    public static void regenStamina(Player player) {
        setStamina(player, getStamina(player) + 1);
    }

    public static void lockSprinting(Player player) {
        player.getPersistentData().putBoolean(STAMINA_LOCKED, true);
    }

    public static void unlockSprinting(Player player) {
        player.getPersistentData().putBoolean(STAMINA_LOCKED, false);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || !(event.player instanceof ServerPlayer player)
                || event.phase.equals(TickEvent.Phase.START))
            return;

        boolean shouldSync = false;

        if (!player.getPersistentData().contains(STAMINA))
            setStamina(player, getMaxStamina(player));

        if (player.isSprinting() && player.getVehicle() == null && !player.getAbilities().instabuild) {
            //If the vigour effect is active give the player 20% chance per level to not consume stamina when running.
            if (player.hasEffect(SRMobEffects.VIGOUR.get())) {
                MobEffectInstance mobEffectInstance = player.getEffect(SRMobEffects.VIGOUR.get());
                //noinspection ConstantConditions
                if (player.getRandom().nextDouble() < 0.2d * (mobEffectInstance.getAmplifier() + 1))
                    return;
            }
            consumeStamina(player);
            if (getStamina(player) <= 0)
                lockSprinting(player);
            shouldSync = true;
        } else if ((getStamina(player) != getMaxStamina(player) && getMaxStamina(player) >= 40)
                //Trigger the sync for clients
                || player.tickCount == 1) {
            //Slower regeneration if stamina il locked
            if (isStaminaLocked(player) && player.tickCount % 3 == 0)
                return;
            regenStamina(player);
            if (getStamina(player) >= getMaxStamina(player) - 10)
                unlockSprinting(player);
            shouldSync = true;
        }
        else if (!isStaminaLocked(player) && getMaxStamina(player) < 40) {
            setStamina(player, 0);
            lockSprinting(player);
            shouldSync = true;
        }

        if (shouldSync) {
            //Sync stamina to client
            Object msg = new MessageStaminaSync(getStamina(player), isStaminaLocked(player));
            NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @SubscribeEvent
    public void onSprint(PlayerSprintEvent event) {
        if (!this.isEnabled()
                || event.getPlayer().getAbilities().instabuild)
            return;

        if (!canSprint(event.getPlayer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerJump(final LivingEvent.LivingJumpEvent event) {
        if (!this.isEnabled()
                || staminaConsumedOnJump == 0
                || !(event.getEntity() instanceof ServerPlayer player))
            return;

        consumeStamina(player, staminaConsumedOnJump);
    }

    static ResourceLocation PLAYER_HEALTH_ELEMENT = new ResourceLocation("minecraft", "player_health");

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Post event) {
        if (!this.isEnabled())
            return;
        if (event.getOverlay() == GuiOverlayManager.findOverlay(PLAYER_HEALTH_ELEMENT)) {
            Minecraft mc = Minecraft.getInstance();
            ForgeGui gui = (ForgeGui) mc.gui;
            if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
                renderStamina(gui, event.getPoseStack(), event.getPartialTick(), event.getWindow().getScreenWidth(), event.getWindow().getScreenHeight());
            }
        }
    }

    private static final Vec2 UV_STAMINA = new Vec2(0, 9);

    @OnlyIn(Dist.CLIENT)
    public static void renderStamina(ForgeGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
        int healthIconsOffset = gui.leftHeight;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        int right = mc.getWindow().getGuiScaledWidth() / 2 - 91;
        int top = mc.getWindow().getGuiScaledHeight() - healthIconsOffset + 9;
        int halfHeartsMaxStamina = Mth.ceil((float) getMaxStamina(player) / staminaPerHalfHeart);
        int halfHeartsStamina = Mth.ceil((float) getStamina(player) / staminaPerHalfHeart);
        RenderSystem.setShaderTexture(0, SurvivalReimagined.GUI_ICONS);
        int height = 9;
        if (isStaminaLocked(player))
            setColor(1f, 1f, 1f, .5f);
        else
            setColor(1f, 0f, 0f, .75f);
        for (int h = 0; h < halfHeartsMaxStamina; h++) {
            if (h < halfHeartsStamina)
                continue;
            int v = (int) UV_STAMINA.y;
            int width;
            int u;
            int r = 0;
            if (h % 2 == 0) {
                width = 5;
                u = (int) UV_STAMINA.x;
            }
            else {
                width = 4;
                u = (int) UV_STAMINA.x + 5;
                r = 5;
            }

            mc.gui.blit(poseStack, right + (h / 2 * 8) + r, top, u, v, width, height);
        }
        resetColor();

        // rebind default icons
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
    }

    public static void setColor(float r, float g, float b, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, alpha);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void resetColor() {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void debugScreen(CustomizeGuiOverlayEvent.DebugText event) {
        if (!this.isEnabled())
            return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer playerEntity = mc.player;
        if (playerEntity == null)
            return;
        if (mc.options.renderDebug && !mc.showOnlyReducedInfo()) {
            event.getLeft().add(String.format("Stamina: %d/%d; Locked: %s", getStamina(playerEntity), getMaxStamina(playerEntity), isStaminaLocked(playerEntity)));
        }
    }
}
