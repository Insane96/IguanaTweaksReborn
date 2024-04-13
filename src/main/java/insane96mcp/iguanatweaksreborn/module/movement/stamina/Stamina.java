package insane96mcp.iguanatweaksreborn.module.movement.stamina;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.event.ITEEventFactory;
import insane96mcp.iguanatweaksreborn.mixin.client.GuiMixin;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.NetworkHandler;
import insane96mcp.iguanatweaksreborn.network.message.StaminaSync;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.utils.ClientUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.event.PlayerSprintEvent;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

@Label(name = "Stamina", description = "Stamina to let the player run and do stuff.")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class Stamina extends Feature {
    public static final UUID SLOWDOWN_UUID = UUID.fromString("b17cbf02-97f8-4c50-9cd1-6dc732593fed");

    public static final String STAMINA = IguanaTweaksReborn.RESOURCE_PREFIX + "stamina";
    public static final String STAMINA_LOCKED = IguanaTweaksReborn.RESOURCE_PREFIX + "stamina_locked";
    public static String OVERLAY = "stamina_overlay";

    public static final RegistryObject<Enchantment> VIGOUR = ITRRegistries.ENCHANTMENTS.register("vigour", Vigour::new);

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

    @Config(min = 0, max = 1)
    @Label(name = "Slowdown.Threshold", description = "Below this percentage stamina you'll get slowed down.")
    public static Double slowdownThreshold = 0.2;
    @Config(min = 0)
    @Label(name = "Slowdown.Flat Threshold", description = "Below this stamina you'll get slowed down.")
    public static Double slowdownFlatThreshold = 20d;
    @Config(min = -1)
    @Label(name = "Slowdown.Amount")
    public static Double slowdownAmount = -0.2;
    @Config
    @Label(name = "Slowdown.Only when locked")
    public static Boolean slowdownOnlyWhenLocked = true;

    @Config
    @Label(name = "Disable Sprinting", description = "Disable sprinting (and swimming) altogether")
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
            int vigourEnchLvl = EnchantmentHelper.getEnchantmentLevel(VIGOUR.get(), player);
            if (vigourEnchLvl > 0)
                staminaToConsume *= (1 - vigourEnchLvl * (0.25f - (0.05f * (vigourEnchLvl - 1))));
            staminaToConsume = ITEEventFactory.onStaminaConsumed(player, staminaToConsume);
            if (staminaToConsume == 0)
                return;
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

            staminaToRecover = ITEEventFactory.onStaminaRegenerated(player, staminaToRecover);
            if (staminaToRecover == 0)
                return;
            stamina = StaminaHandler.regenStamina(player, staminaToRecover);
            if (isStaminaLocked && stamina >= maxStamina * unlockStaminaAtHealthRatio) {
                StaminaHandler.unlockSprinting(player);
                isStaminaLocked = false;
            }
            shouldSync = true;
        }
        else if (!isStaminaLocked && maxStamina < staminaPerHalfHeart * 5) {
            StaminaHandler.setStamina(player, 0);
            StaminaHandler.lockSprinting(player);
            isStaminaLocked = true;
            shouldSync = true;
        }
        slowdown(player, stamina, stamina / maxStamina, isStaminaLocked);

        if (shouldSync) {
            //Sync stamina to client
            Object msg = new StaminaSync((int) StaminaHandler.getStamina(player), StaminaHandler.isStaminaLocked(player));
            NetworkHandler.CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void slowdown(Player player, float stamina, float staminaPercentage, boolean isLocked) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SLOWDOWN_UUID);
        if ((!isLocked && slowdownOnlyWhenLocked)
                || (staminaPercentage > slowdownThreshold && stamina > slowdownFlatThreshold))
            return;
        MCUtils.applyModifier(player, Attributes.MOVEMENT_SPEED, SLOWDOWN_UUID, "Stamina slowdown", slowdownAmount, AttributeModifier.Operation.MULTIPLY_BASE, false);
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
        int vigourEnchLvl = EnchantmentHelper.getEnchantmentLevel(VIGOUR.get(), player);
        if (vigourEnchLvl > 0)
            consumed *= (1 - vigourEnchLvl * 0.2f);
        StaminaHandler.consumeStamina(player, consumed);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), OVERLAY, (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
            if (isEnabled(Stamina.class) && gui.shouldDrawSurvivalElements())
                renderStamina(gui, guiGraphics);
        });
    }

    private static final Vec2 UV_STAMINA = new Vec2(0, 9);
    protected static final RandomSource RANDOM = RandomSource.create();

    @OnlyIn(Dist.CLIENT)
    public static void renderStamina(ForgeGui gui, GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        RANDOM.setSeed(gui.getGuiTicks() * 312871L);

        int health = Mth.ceil(player.getHealth());
        int healthLast = ((GuiMixin)gui).getDisplayHealth();

        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = Math.max((float) attrMaxHealth.getValue(), Math.max(healthLast, health));
        int healthMaxI = Mth.ceil(healthMax);
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
        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION))
            regen = gui.getGuiTicks() % Mth.ceil(healthMax + 5.0F);

        if (StaminaHandler.isStaminaLocked(player))
            ClientUtils.setRenderColor(0.8f, 0.8f, 0.8f, .8f);
        else
            ClientUtils.setRenderColor(1f, 1f, 1f, 0.5f);
        int jiggle = 0;

        for (int hp = healthMaxI - 1; hp >= 0; hp--) {
            //Doesn't work with absorption ...
            if ((hp + 1) % 2 == 0) {
                if (health + absorb <= 4)
                    jiggle = RANDOM.nextInt(2);
                //TODO ...
                /*if (hp + 1 == regen)
                    jiggle -= 2;*/
            }
            if (hp >= halfHeartsMaxStamina || hp < halfHeartsStamina)
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

            guiGraphics.blit(IguanaTweaksReborn.GUI_ICONS, right + (hp / 2 * 8) + r - (hp / 20 * 80), top - (hp / 20 * rowHeight) + jiggle, u, v, width, height);
        }
        ClientUtils.resetRenderColor();
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
