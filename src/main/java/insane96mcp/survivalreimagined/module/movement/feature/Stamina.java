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
import insane96mcp.survivalreimagined.module.movement.handler.StaminaHandler;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
    public static Integer staminaConsumedOnJump = 5;

    @Config(min = 0)
    @Label(name = "Stamina on hit")
    public static Integer staminaConsumedOnHit = 20;

    @Config
    @Label(name = "Disable Sprinting", description = "Disable sprinting altogether")
    public static Boolean disableSprinting = false;

    public Stamina(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isEnabled()
                || !(event.player instanceof ServerPlayer player)
                || event.phase.equals(TickEvent.Phase.START))
            return;

        boolean shouldSync = false;

        if (!player.getPersistentData().contains(STAMINA))
            StaminaHandler.setStamina(player, StaminaHandler.getMaxStamina(player));

        if (player.isSprinting() && player.getVehicle() == null && !player.getAbilities().instabuild) {
            int amountConsumed = 1;
            //If the vigour effect is active give the player 20% chance per level to not consume stamina when running.
            if (player.hasEffect(SRMobEffects.VIGOUR.get())) {
                MobEffectInstance vigourInstance = player.getEffect(SRMobEffects.VIGOUR.get());
                //noinspection DataFlowIssue
                if (player.getRandom().nextDouble() < 0.2d * (vigourInstance.getAmplifier() + 1))
                    return;
            }
            //If the tired effect is active consume one more stamina per level above I
            if (player.hasEffect(SRMobEffects.TIRED.get())) {
                MobEffectInstance tiredInstance = player.getEffect(SRMobEffects.TIRED.get());
                //noinspection DataFlowIssue
                amountConsumed += tiredInstance.getAmplifier();
            }
            if (player.getPose() == Pose.SWIMMING && player.tickCount % 3 == 1)
                return;
            StaminaHandler.consumeStamina(player, amountConsumed);
            shouldSync = true;
        } else if ((StaminaHandler.getStamina(player) != StaminaHandler.getMaxStamina(player) && StaminaHandler.getMaxStamina(player) >= 40)
                //Trigger the sync for clients
                || player.tickCount == 1) {
            //Slower regeneration if stamina il locked
            if (StaminaHandler.isStaminaLocked(player) && player.tickCount % 3 == 0)
                return;
            StaminaHandler.regenStamina(player);
            if (StaminaHandler.getStamina(player) >= StaminaHandler.getMaxStamina(player) - 10)
                StaminaHandler.unlockSprinting(player);
            shouldSync = true;
        }
        else if (!StaminaHandler.isStaminaLocked(player) && StaminaHandler.getMaxStamina(player) < 40) {
            StaminaHandler.setStamina(player, 0);
            StaminaHandler.lockSprinting(player);
            shouldSync = true;
        }

        if (shouldSync) {
            //Sync stamina to client
            Object msg = new MessageStaminaSync(StaminaHandler.getStamina(player), StaminaHandler.isStaminaLocked(player));
            NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSprint(PlayerSprintEvent event) {
        if (!this.isEnabled()
                || event.getPlayer().getAbilities().instabuild)
            return;

        if (!StaminaHandler.canSprint(event.getPlayer()) || disableSprinting)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerJump(final LivingEvent.LivingJumpEvent event) {
        if (!this.isEnabled()
                || staminaConsumedOnJump == 0
                || !(event.getEntity() instanceof ServerPlayer player))
            return;

        int consumed = staminaConsumedOnJump;
        if (player.hasEffect(SRMobEffects.VIGOUR.get()))
            consumed *= 1f - (player.getEffect(SRMobEffects.VIGOUR.get()).getAmplifier() + 1) * 0.2f;
        StaminaHandler.consumeStamina(player, consumed);
    }

    @SubscribeEvent
    public void onPlayerPunch(final AttackEntityEvent event) {
        if (!this.isEnabled()
                || staminaConsumedOnHit == 0
                || !(event.getTarget() instanceof LivingEntity)
                || event.getEntity().level.isClientSide)
            return;

        int consumed = staminaConsumedOnHit;
        if (event.getEntity().hasEffect(SRMobEffects.VIGOUR.get()))
            consumed *= 1f - (event.getEntity().getEffect(SRMobEffects.VIGOUR.get()).getAmplifier() + 1) * 0.2f;
        StaminaHandler.consumeStamina(event.getEntity(), consumed);
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
    //protected static final RandomSource random = RandomSource.create();

    @OnlyIn(Dist.CLIENT)
    public static void renderStamina(ForgeGui gui, PoseStack poseStack, float partialTicks, int screenWidth, int screenHeight) {
        int healthIconsOffset = 49;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        //random.setSeed(gui.getGuiTicks() * 312871L);

        int right = mc.getWindow().getGuiScaledWidth() / 2 - 91;
        int top = mc.getWindow().getGuiScaledHeight() - healthIconsOffset + 9;
        int halfHeartsMaxStamina = Mth.ceil((float) StaminaHandler.getMaxStamina(player) / staminaPerHalfHeart);
        int halfHeartsStamina = Mth.ceil((float) StaminaHandler.getStamina(player) / staminaPerHalfHeart);
        RenderSystem.setShaderTexture(0, SurvivalReimagined.GUI_ICONS);
        int height = 9;
        if (StaminaHandler.isStaminaLocked(player))
            setColor(0.7f, 0.7f, 0.7f, .7f);
        else
            setColor(1f, 1f, 1f, 0.43f);
        for (int hp = halfHeartsMaxStamina - 1; hp >= 0; hp--) {
            if (hp < halfHeartsStamina)
                continue;
            int v = (int) UV_STAMINA.y;
            int width;
            int u;
            int r = 0;
            if (hp % 2 == 0) {
                width = 5;
                u = (int) UV_STAMINA.x;
            }
            else {
                width = 4;
                u = (int) UV_STAMINA.x + 5;
                r = 5;
            }
            /*if (player.getHealth() <= 4) {
                top += random.nextInt(2);
            }*/

            GuiComponent.blit(poseStack, right + (hp / 2 * 8) + r - (hp / 20 * 80), top - (hp / 20 * 10), u, v, width, height);
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
            event.getLeft().add(String.format("Stamina: %d/%d; Locked: %s", StaminaHandler.getStamina(playerEntity), StaminaHandler.getMaxStamina(playerEntity), StaminaHandler.isStaminaLocked(playerEntity)));
        }
    }
}
