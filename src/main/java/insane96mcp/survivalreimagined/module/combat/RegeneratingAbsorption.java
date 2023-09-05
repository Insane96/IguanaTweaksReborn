package insane96mcp.survivalreimagined.module.combat;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Regenerating Absorption", description = "Adds a new attribute to add regenerating absorption hearts to the player.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class RegeneratingAbsorption extends Feature {

    public static final RegistryObject<Attribute> ATTRIBUTE = SRRegistries.ATTRIBUTES.register("regenerating_absorption", () -> new RangedAttribute("attribute.name.regenerating_absorption", 0d, 0d, 1024d));

    @Config(min = 0)
    @Label(name = "Regen Speed", description = "Speed (in seconds) at which Absorption hearts regenerate")
    public static Integer regenSpeed = 10;
    @Config(min = 0)
    @Label(name = "Absorption decay", description = "Speed (in seconds) at which Absorption hearts decay")
    public static Integer absorptionDecay = 1;
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
            if (event.has(entityType, ATTRIBUTE.get()))
                continue;

            event.add(entityType, ATTRIBUTE.get());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide
                || event.getEntity().tickCount % 20 != 0)
            return;

        LivingEntity entity = event.getEntity();

        double goldenAbsorption = entity.getAttributeValue(ATTRIBUTE.get());
        //Take into account absorption effect
        int absorptionAmplifier = 0;
        if (entity.hasEffect(MobEffects.ABSORPTION)) {
            absorptionAmplifier = entity.getEffect(MobEffects.ABSORPTION).getAmplifier() + 1;
        }

        float actualGoldenAbsorption = entity.getAbsorptionAmount() - (absorptionAmplifier * 4);

        if (actualGoldenAbsorption >= 0f && actualGoldenAbsorption != goldenAbsorption) {
            if (actualGoldenAbsorption > goldenAbsorption && entity.tickCount % (absorptionDecay * 20) == 0) {
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() - 1);
            }
            else if (entity.tickCount % (regenSpeed * 20) == 0) {
                double newAbsorption = Math.min(entity.getAbsorptionAmount() + 1, entity.getAttributeValue(ATTRIBUTE.get()) + (absorptionAmplifier * 4));
                if (capToHealth)
                    newAbsorption = Math.min(newAbsorption, entity.getHealth() + (absorptionAmplifier * 4));
                entity.setAbsorptionAmount((float) newAbsorption);
            }
        }
    }

    public static boolean entityAbsorption() {
        return isEnabled(RegeneratingAbsorption.class) && absorbingEntityDamageOnly;
    }
}
