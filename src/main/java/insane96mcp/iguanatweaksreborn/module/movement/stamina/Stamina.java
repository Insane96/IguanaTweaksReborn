package insane96mcp.iguanatweaksreborn.module.movement.stamina;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.mixin.client.GuiMixin;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.network.message.StaminaSync;
import insane96mcp.iguanatweaksreborn.utils.ClientUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.event.PlayerSprintEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;

@Label(name = "Stamina", description = "Stamina to let the player run and do stuff.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class Stamina extends Feature {

    public static final String STAMINA = IguanaTweaksReborn.RESOURCE_PREFIX + "stamina";
    public static final String STAMINA_LOCKED = IguanaTweaksReborn.RESOURCE_PREFIX + "stamina_locked";

    @Config(min = 0)
    @Label(name = "Stamina per half heart", description = "How much stamina the player has per half heart. Each 1 stamina is 1 tick of running")
    public static Integer staminaPerHalfHeart = 5;

    @Config(min = 0)
    @Label(name = "Stamina consumed on jump", description = "How much stamina the player consumes on each jump")
    public static Integer staminaConsumedOnJump = 5;

    @Config(min = 0)
    @Label(name = "Stamina consumed on swimming", description = "How much stamina the player consumes each tick when swimming")
    public static Double staminaConsumedOnSwimming = 0.5d;

    @Config(min = 0, max = 1d)
    @Label(name = "Unlock Stamina at health ratio", description = "At which health percentage will stamina be unlocked")
    public static Double unlockStaminaAtHealthRatio = 0.75d;

    @Config(min = 0d)
    @Label(name = "Stamina regen per tick")
    public static Double staminaRegenPerTick = 0.6d;

    @Config(min = 0d)
    @Label(name = "Stamina regen per tick if locked")
    public static Double staminaRegenPerTickIfLocked = 0.35d;

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
                || event.phase.equals(TickEvent.Phase.START)
                || disableSprinting)
            return;

        boolean shouldSync = false;

        float maxStamina = StaminaHandler.getMaxStamina(player);
        float stamina = StaminaHandler.getStamina(player);
        boolean isStaminaLocked = StaminaHandler.isStaminaLocked(player);

        //Trigger sync for newly spawned players
        if (player.tickCount == 1)
            shouldSync = true;
        if (player.isSprinting() && player.getVehicle() == null && !player.getAbilities().instabuild) {
            float staminaToConsume = 1f;
            if (player.getPose() == Pose.SWIMMING)
                staminaToConsume = staminaConsumedOnSwimming.floatValue();
            float percIncrease = 0f;
            for (MobEffectInstance instance : player.getActiveEffects()) {
                if (instance.getEffect() instanceof IStaminaModifier staminaModifier)
                    percIncrease += staminaModifier.consumedStaminaModifier(instance.getAmplifier());
            }

            staminaToConsume += (staminaToConsume * percIncrease);
            StaminaHandler.consumeStamina(player, staminaToConsume);
            shouldSync = true;
        }
        else if ((stamina != maxStamina && maxStamina >= staminaPerHalfHeart * 5)) {
            float staminaToRecover = staminaRegenPerTick.floatValue();
            //Slower regeneration if stamina is locked
            if (isStaminaLocked)
                staminaToRecover = staminaRegenPerTickIfLocked.floatValue();
            float percIncrease = 0f;

            for (MobEffectInstance instance : player.getActiveEffects()) {
                if (instance.getEffect() instanceof IStaminaModifier staminaModifier)
                    percIncrease += staminaModifier.regenStaminaModifier(instance.getAmplifier());
            }
            //If max health is higher than 20 then increase stamina regen
            if (maxStamina > staminaPerHalfHeart * 20) {
                percIncrease += (maxStamina - staminaPerHalfHeart * 20) / (staminaPerHalfHeart * 20);
            }
            staminaToRecover += (staminaToRecover * percIncrease);
            stamina = StaminaHandler.regenStamina(player, staminaToRecover);
            if (isStaminaLocked && stamina >= maxStamina * unlockStaminaAtHealthRatio)
                StaminaHandler.unlockSprinting(player);
            shouldSync = true;
        }
        else if (!isStaminaLocked && maxStamina < staminaPerHalfHeart * 5) {
            StaminaHandler.setStamina(player, 0);
            StaminaHandler.lockSprinting(player);
            shouldSync = true;
        }

        if (shouldSync) {
            //Sync stamina to client
            Object msg = new StaminaSync((int) StaminaHandler.getStamina(player), StaminaHandler.isStaminaLocked(player));
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

        float consumed = staminaConsumedOnJump;
        float percIncrease = 0f;
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance.getEffect() instanceof IStaminaModifier staminaModifier)
                percIncrease += staminaModifier.consumedStaminaModifier(instance.getAmplifier());
        }
        consumed += (consumed * percIncrease);
        StaminaHandler.consumeStamina(player, consumed);
    }

    static ResourceLocation PLAYER_HEALTH_ELEMENT = new ResourceLocation("minecraft", "player_health");

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "stamina_overlay", (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
            if (isEnabled(Stamina.class) && gui.shouldDrawSurvivalElements() && gui.shouldDrawSurvivalElements())
                renderStamina(gui, guiGraphics);
        });
    }

    private static final Vec2 UV_STAMINA = new Vec2(0, 9);
    //protected static final RandomSource random = RandomSource.create();

    @OnlyIn(Dist.CLIENT)
    public static void renderStamina(ForgeGui gui, GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        //random.setSeed(gui.getGuiTicks() * 312871L);

        int health = Mth.ceil(player.getHealth());
        int healthLast = ((GuiMixin)gui).getDisplayHealth();

        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
        int absorb = Mth.ceil(player.getAbsorptionAmount());

        int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int leftHeight = gui.leftHeight;
        leftHeight -= (healthRows * rowHeight);
        if (rowHeight != 10)
            leftHeight -= 10 - rowHeight;

        int right = mc.getWindow().getGuiScaledWidth() / 2 - 91;
        int top = mc.getWindow().getGuiScaledHeight() - leftHeight - 1;
        int halfHeartsMaxStamina = Mth.ceil(StaminaHandler.getMaxStamina(player) / staminaPerHalfHeart);
        int halfHeartsStamina = Mth.ceil(StaminaHandler.getStamina(player) / staminaPerHalfHeart);
        int height = 9;
        if (StaminaHandler.isStaminaLocked(player))
            ClientUtils.setColor(0.8f, 0.8f, 0.8f, .8f);
        else
            ClientUtils.setColor(1f, 1f, 1f, 0.5f);
        //int jiggle = 0;
        //TODO Change to the same way vanilla renders hearts
        for (int hp = halfHeartsMaxStamina - 1; hp >= 0; hp--) {
            /*if (player.getHealth() + player.getAbsorptionAmount() <= 4 && ((hp + 1) % 2 == 0 || hp == halfHeartsMaxStamina - 1)) {
                jiggle = random.nextInt(2);
            }*/
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

            //top += jiggle;

            guiGraphics.blit(IguanaTweaksReborn.GUI_ICONS, right + (hp / 2 * 8) + r - (hp / 20 * 80), top - (hp / 20 * rowHeight), u, v, width, height);
        }
        ClientUtils.resetColor();
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
            event.getLeft().add(String.format("Stamina: %.1f/%.1f; Locked: %s", StaminaHandler.getStamina(playerEntity), StaminaHandler.getMaxStamina(playerEntity), StaminaHandler.isStaminaLocked(playerEntity)));
        }
    }
}
