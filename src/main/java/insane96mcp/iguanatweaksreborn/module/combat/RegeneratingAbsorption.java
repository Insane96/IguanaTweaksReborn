package insane96mcp.iguanatweaksreborn.module.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.movement.stamina.Stamina;
import insane96mcp.iguanatweaksreborn.network.message.RegenAbsorptionSync;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Regenerating Absorption", description = "Adds a new attribute to add regenerating absorption hearts to the player.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class RegeneratingAbsorption extends Feature {

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(IguanaTweaksReborn.MOD_ID, "textures/gui/absorption.png");
    public static final String REGEN_ABSORPTION_TAG = IguanaTweaksReborn.RESOURCE_PREFIX + "regen_absorption";
    public static final String HURT_COOLDOWN_TAG = IguanaTweaksReborn.RESOURCE_PREFIX + "regen_absorption_hurt_cooldown";
    public static final String NO_HURT_SOUND_TAG = IguanaTweaksReborn.RESOURCE_PREFIX + "no_hurt_sound";

    public static final RegistryObject<Attribute> ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption", () -> new RangedAttribute("attribute.name.regenerating_absorption", 0d, 0d, 1024d));

    public static final RegistryObject<Attribute> SPEED_ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption_speed", () -> new RangedAttribute("attribute.name.regenerating_absorption_speed", 0.200d, 0d, 20d));

    public static final RegistryObject<MobEffect> EFFECT = ITRRegistries.MOB_EFFECTS.register("regenerating_absorption", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ATTRIBUTE.get(), "704d7291-63ba-4346-8aa8-a08e90a13fdf", 4, AttributeModifier.Operation.ADDITION));

    @Config(min = 0)
    @Label(name = "Un-damaged time to regen", description = "Ticks that must pass from the last hit to regen absorption hearts. This is affected by regenerating absorption speed (absorp regen speed * this)")
    public static Integer unDamagedTimeToRegen = 150;
    @Config(min = 0)
    @Label(name = "Un-damaged time to regen cap", description = "Min Un-damaged time to regen")
    public static Integer unDamagedTimeToRegenCap = 60;
    @Config
    @Label(name = "Cap to health", description = "The amount of regenerating absorption hearts cannot go over the entity's current health.")
    public static Boolean capToHealth = true;
    @Config
    @Label(name = "Absorbing bypasses_armor damage only", description = "If true, absorption hearts will not shield from damages in the bypasses_armor damage type tag.")
    public static Boolean absorbingDamageTypeTagOnly = true;
    @Config
    @Label(name = "Sound on absorption hurt", description = "If true, a sound is played when the absorption is damaged.")
    public static Boolean soundOnAbsorptionHurt = true;
    @Config
    @Label(name = "Render on the right", description = "If true, regenerating absorption hearts are rendered on the right instead on top of hearts.")
    public static Boolean renderOnRight = false;

    public RegeneratingAbsorption(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static void addAttribute(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            if (!event.has(entityType, ATTRIBUTE.get()))
                event.add(entityType, ATTRIBUTE.get());
            if (!event.has(entityType, SPEED_ATTRIBUTE.get()))
                event.add(entityType, SPEED_ATTRIBUTE.get());
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide
                || event.getEntity().isDeadOrDying())
            return;

        LivingEntity entity = event.getEntity();
        int hurtCooldown = entity.getPersistentData().getInt(HURT_COOLDOWN_TAG);
        if (hurtCooldown > 0) {
            hurtCooldown--;
            entity.getPersistentData().putInt(HURT_COOLDOWN_TAG, hurtCooldown);
            return;
        }
        float maxAbsorption = (float) entity.getAttributeValue(ATTRIBUTE.get());
        float regenSpeed = (float) (entity.getAttributeValue(SPEED_ATTRIBUTE.get()) / 20f);

        float currentAbsorption = entity.getPersistentData().getFloat(REGEN_ABSORPTION_TAG);
        if (capToHealth)
            maxAbsorption = Math.min(maxAbsorption, Mth.ceil(entity.getHealth()));
        if (currentAbsorption < 0f || currentAbsorption == maxAbsorption)
            return;

        if (currentAbsorption > maxAbsorption)
            currentAbsorption = Math.max(currentAbsorption - 0.1f, 0f);
        else
            currentAbsorption = Math.min(currentAbsorption + regenSpeed, maxAbsorption);

        if (entity instanceof ServerPlayer player)
            RegenAbsorptionSync.sync(player, currentAbsorption);
        entity.getPersistentData().putFloat(REGEN_ABSORPTION_TAG, currentAbsorption);
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide
                || unDamagedTimeToRegen == 0
                || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        double absorptionSpeed = event.getEntity().getAttributeValue(SPEED_ATTRIBUTE.get());
        event.getEntity().getPersistentData().putInt(HURT_COOLDOWN_TAG, (int) Math.max(unDamagedTimeToRegen * (1f - absorptionSpeed), unDamagedTimeToRegenCap));
    }

    @SubscribeEvent
    public void onLivingHurtPreAbsorption(LivingHurtEvent event) {
        if (!this.isEnabled()
                || !canDamageAbsorption(event.getSource()))
            return;

        float currentAbsorption = event.getEntity().getPersistentData().getFloat(REGEN_ABSORPTION_TAG);
        if (currentAbsorption <= 0)
            return;
        //if (currentAbsorption < event.getAmount())
            //event.getEntity().level().playSound(null, event.getEntity(), SoundEvents.GENERIC_EXPLODE, event.getEntity() instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 0.5f, 2f);
        //else
            //event.getEntity().getPersistentData().putBoolean(NO_HURT_SOUND_TAG, true);
        float toRemove = Math.min(currentAbsorption, event.getAmount());
        currentAbsorption -= toRemove;
        event.setAmount(event.getAmount() - toRemove);
        event.getEntity().getPersistentData().putFloat(REGEN_ABSORPTION_TAG, currentAbsorption);
        if (soundOnAbsorptionHurt)
            event.getEntity().level().playSound(null, event.getEntity(), SoundEvents.SHIELD_BREAK, event.getEntity() instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1f, 2f);
        if (event.getEntity() instanceof ServerPlayer player)
            RegenAbsorptionSync.sync(player, currentAbsorption);
    }

    public static boolean canDamageAbsorption(DamageSource source) {
        if (!absorbingDamageTypeTagOnly)
            return true;
        return source.getEntity() != null && !source.is(DamageTypeTags.BYPASSES_ARMOR);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderGuiOverlayPre(RegisterGuiOverlaysEvent event) {
        event.registerAbove(new ResourceLocation(IguanaTweaksReborn.MOD_ID, Stamina.OVERLAY), "regenerating_absorption", (gui, guiGraphics, partialTicks, screenWidth, screenHeight) -> {
            if (isEnabled(RegeneratingAbsorption.class) && gui.shouldDrawSurvivalElements() && gui.shouldDrawSurvivalElements())
                renderAbsorption(guiGraphics, screenWidth, screenHeight);
        });
    }

    @OnlyIn(Dist.CLIENT)
    protected static void renderAbsorption(GuiGraphics guiGraphics, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        ForgeGui gui = (ForgeGui) mc.gui;
        mc.getProfiler().push("armor");

        RenderSystem.enableBlend();
        int left = width / 2 + (!renderOnRight ? -91 : 82);
        int top = height - (!renderOnRight ? gui.leftHeight : gui.rightHeight);

        int level = Mth.ceil(mc.player.getPersistentData().getFloat(REGEN_ABSORPTION_TAG));
        for (int i = 1; i <= level; i += 2)
        {
            if (i == level)
                guiGraphics.blit(GUI_ICONS, left, top, 9, 0, 9, 9, 18, 9);
            else
                guiGraphics.blit(GUI_ICONS, left, top, 0, 0, 9, 9, 18, 9);
            //else
            //guiGraphics.blit(GUI_ICONS, left, top, 0, 0, 0, 9, 256, 256);
            if (i % 19 == 0) {
                left = width / 2 - 91;
                top -= 10;
                gui.leftHeight += 10;
            }
            else
                left += 8;
        }
        if (level > 0)
            gui.leftHeight += 10;

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }
}
