package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Innate Resistance", description = "Adds a new attribute that reduces damage taken. The actual damage reduction decreases / increases in easy / hard.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class InnateResistance extends Feature {

    public static final RegistryObject<Attribute> ATTRIBUTE = ITRRegistries.ATTRIBUTES.register("innate_resistance", () -> new RangedAttribute("attribute.name.innate_resistance", 0d, 0d, 1024d));

    public static final RegistryObject<MobEffect> EFFECT = ITRRegistries.MOB_EFFECTS.register("innate_resistance", () -> new ILMobEffect(MobEffectCategory.BENEFICIAL, 0x818894)
            .addAttributeModifier(ATTRIBUTE.get(), "704d7291-63ba-4346-8aa8-a08e90a13fdf", 0.4, AttributeModifier.Operation.ADDITION));

    public InnateResistance(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static void addAttribute(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            if (!event.has(entityType, ATTRIBUTE.get()))
                event.add(entityType, ATTRIBUTE.get());
        }
    }

    @SubscribeEvent
    public void onLivingTick(LivingHurtEvent event) {
        if (!this.isEnabled()
                || event.getEntity().level().isClientSide
                || event.getSource().is(DamageTypeTags.BYPASSES_ARMOR))
            return;

        LivingEntity entity = event.getEntity();
        float damageReduction = (float) entity.getAttributeValue(ATTRIBUTE.get());
        if (entity.level().getDifficulty().equals(Difficulty.EASY))
            damageReduction *= 0.5f;
        else if (entity.level().getDifficulty().equals(Difficulty.HARD))
            damageReduction *= 1.5f;
        event.setAmount(event.getAmount() - damageReduction);
    }
}
