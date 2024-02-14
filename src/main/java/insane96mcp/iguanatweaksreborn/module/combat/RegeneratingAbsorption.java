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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Regenerating Absorption", description = "Adds a new attribute to add regenerating absorption hearts to the player.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class RegeneratingAbsorption extends Feature {

    private static final String HURT_COOLDOWN = IguanaTweaksReborn.RESOURCE_PREFIX + "regen_absorption_hurt_cooldown";

    public static final RegistryObject<Attribute> ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption", () -> new RangedAttribute("attribute.name.regenerating_absorption", 0d, 0d, 1024d));

    public static final RegistryObject<Attribute> SPEED_ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("regenerating_absorption_speed", () -> new RangedAttribute("attribute.name.regenerating_absorption_speed", 0.200d, 0d, 20d));

    public static final RegistryObject<MobEffect> EFFECT = ITRRegistries.MOB_EFFECTS.register("regenerating_absorption", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ATTRIBUTE.get(), "704d7291-63ba-4346-8aa8-a08e90a13fdf", 4, AttributeModifier.Operation.ADDITION).addAttributeModifier(SPEED_ATTRIBUTE.get(), "704d7291-63ba-4346-8aa8-a08e90a13fdf", 0.1f, AttributeModifier.Operation.MULTIPLY_BASE));

    @Config(min = 0)
    @Label(name = "Un-damaged time to regen", description = "Ticks that must pass from the last hit to regen absorption hearts. This is affected by regenerating absorption speed (absorp regen speed * this)")
    public static Integer unDamagedTimeToRegen = 50;
    @Config
    @Label(name = "Cap to health", description = "The amount of regenerating absorption hearts cannot go over the entity's current health.")
    public static Boolean capToHealth = true;
    @Config
    @Label(name = "Absorbing bypasses_armor damage only", description = "If true, absorption hearts will not shield from damages in the bypasses_armor damage type tag.")
    public static Boolean absorbingDamageTypeTagOnly = true;

    public RegeneratingAbsorption(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static void regeneratingAbsorptionAttribute(EntityAttributeModificationEvent event) {
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
        int hurtCooldown = entity.getPersistentData().getInt(HURT_COOLDOWN);
        if (hurtCooldown > 0) {
            hurtCooldown--;
            entity.getPersistentData().putInt(HURT_COOLDOWN, hurtCooldown);
            return;
        }
        double regeneratingAbsorption = entity.getAttributeValue(ATTRIBUTE.get());
        double regenSpeed = entity.getAttributeValue(SPEED_ATTRIBUTE.get()) / 20f;
        if (regenSpeed == 0)
            return;

        //Take into account absorption effect
        int absorptionEffect = 0;
        if (entity.hasEffect(MobEffects.ABSORPTION))
            absorptionEffect = (entity.getEffect(MobEffects.ABSORPTION).getAmplifier() + 1) * 4;

        float actualRegenAbsorption = entity.getAbsorptionAmount() - absorptionEffect;
        regeneratingAbsorption = Math.min(regeneratingAbsorption, Math.ceil(entity.getHealth()));
        if (actualRegenAbsorption < 0f || actualRegenAbsorption == regeneratingAbsorption)
            return;

        if (actualRegenAbsorption > regeneratingAbsorption) {
            entity.setAbsorptionAmount((float) (entity.getAbsorptionAmount() - regenSpeed * 10f));
        }
        else {
                double newAbsorption = Math.min(entity.getAbsorptionAmount() + regenSpeed, entity.getAttributeValue(ATTRIBUTE.get()) + absorptionEffect);
            if (capToHealth)
                newAbsorption = Math.min(newAbsorption, entity.getHealth() + absorptionEffect);
            entity.setAbsorptionAmount((float) newAbsorption);
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (!this.isEnabled()
                || unDamagedTimeToRegen == 0
                || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        double absorptionSpeed = event.getEntity().getAttributeValue(SPEED_ATTRIBUTE.get());
        event.getEntity().getPersistentData().putInt(HURT_COOLDOWN, unDamagedTimeToRegen - (int)(absorptionSpeed * unDamagedTimeToRegen));
    }

    public static boolean damageTypeTagOnly() {
        return isEnabled(RegeneratingAbsorption.class) && absorbingDamageTypeTagOnly;
    }
}
