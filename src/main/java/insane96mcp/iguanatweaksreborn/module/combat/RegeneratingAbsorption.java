package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Regenerating Absorption", description = "Adds a new attribute to add regenerating absorption hearts to the player.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class RegeneratingAbsorption extends Feature {

    private static final String TICK_REGEN_ABSORPTION = IguanaTweaksReborn.RESOURCE_PREFIX + "tick_regen_absorption";
    private static final String HURT_COOLDOWN = IguanaTweaksReborn.RESOURCE_PREFIX + "regen_absorption_hurt_cooldown";

    public static final RegistryObject<Attribute> ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption", () -> new RangedAttribute("attribute.name.regenerating_absorption", 0d, 0d, 1024d));

    public static final RegistryObject<Attribute> REGEN_ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption_speed", () -> new RangedAttribute("attribute.name.regenerating_absorption_speed", 0.200d, 0d, 20d));

    public static final RegistryObject<MobEffect> EFFECT = ITRRegistries.MOB_EFFECTS.register("regenerating_absorption", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ATTRIBUTE.get(), "704d7291-63ba-4346-8aa8-a08e90a13fdf", 4, AttributeModifier.Operation.ADDITION));

    @Config(min = 0)
    @Label(name = "Absorption decay", description = "Speed (in ticks) at which Absorption hearts decay")
    public static Integer absorptionDecay = 10;
    @Config(min = 0)
    @Label(name = "Un-damaged time to regen", description = "Ticks that must pass from the last hit to regen absorption hearts. This is affected by regenerating absorption speed")
    public static Integer unDamagedTimeToRegen = 50;
    @Config
    @Label(name = "Cap to health", description = "The amount of regenerating absorption hearts cannot go over the entity's current health.")
    public static Boolean capToHealth = true;
    @Config
    @Label(name = "Absorbing Entity damage only", description = "If true, absorption hearts will not shield from damages not from mobs (e.g. Poison).")
    public static Boolean absorbingEntityDamageOnly = true;

    public RegeneratingAbsorption(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static void regeneratingAbsorptionAttribute(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            if (!event.has(entityType, ATTRIBUTE.get()))
                event.add(entityType, ATTRIBUTE.get());
            if (!event.has(entityType, REGEN_ATTRIBUTE.get()))
                event.add(entityType, REGEN_ATTRIBUTE.get());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide)
            return;

        LivingEntity entity = event.getEntity();
        int hurtCooldown = entity.getPersistentData().getInt(HURT_COOLDOWN);
        if (hurtCooldown > 0) {
            hurtCooldown--;
            entity.getPersistentData().putInt(HURT_COOLDOWN, hurtCooldown);
            return;
        }
        double regeneratingAbsorption = entity.getAttributeValue(ATTRIBUTE.get());
        int absorptionSpeed = (int) Math.round(20 * (1f / entity.getAttributeValue(REGEN_ATTRIBUTE.get())));
        if (absorptionSpeed == 0)
            return;

        //Take into account absorption effect
        int absorptionAmplifier = 0;
        if (entity.hasEffect(MobEffects.ABSORPTION))
            absorptionAmplifier = entity.getEffect(MobEffects.ABSORPTION).getAmplifier() + 1;

        float actualGoldenAbsorption = entity.getAbsorptionAmount() - (absorptionAmplifier * 4);
        regeneratingAbsorption = Math.min(regeneratingAbsorption, Math.ceil(entity.getHealth()));
        if (actualGoldenAbsorption < 0f || actualGoldenAbsorption == regeneratingAbsorption)
            return;

        int tickRegenAbsorption = entity.getPersistentData().getInt(TICK_REGEN_ABSORPTION);
        tickRegenAbsorption++;
        //int undamagedTicksToRegen = unDamagedTimeToRegen;
        //undamagedTicksToRegen = (int) (undamagedTicksToRegen - (entity.getAttributeValue(REGEN_ATTRIBUTE.get()) * 20f));
        //if (entity instanceof Player player)
            //player.displayClientMessage(Component.literal("regeneratingAbsorption: %s, absorptionSpeed: %s, tickRegenAbsorption: %s, hurtCooldown: %s".formatted(regeneratingAbsorption, absorptionSpeed, tickRegenAbsorption, hurtCooldown)), true);
        if (actualGoldenAbsorption > regeneratingAbsorption && entity.tickCount % absorptionDecay == 0) {
            entity.setAbsorptionAmount(entity.getAbsorptionAmount() - 1);
        }
        else if (tickRegenAbsorption >= absorptionSpeed) {
            double newAbsorption = Math.min(entity.getAbsorptionAmount() + 1, entity.getAttributeValue(ATTRIBUTE.get()) + (absorptionAmplifier * 4));
            if (capToHealth)
                newAbsorption = Math.min(newAbsorption, entity.getHealth() + (absorptionAmplifier * 4));
            entity.setAbsorptionAmount((float) newAbsorption);
            tickRegenAbsorption = 0;
        }
        entity.getPersistentData().putInt(TICK_REGEN_ABSORPTION, tickRegenAbsorption);
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent event) {
        if (!this.isEnabled()
                || unDamagedTimeToRegen == 0
                || event.getSource().getEntity() == null)
            return;

        event.getEntity().getPersistentData().putInt(HURT_COOLDOWN, unDamagedTimeToRegen);
    }

    public static boolean entityAbsorption() {
        return isEnabled(RegeneratingAbsorption.class) && absorbingEntityDamageOnly;
    }
}
