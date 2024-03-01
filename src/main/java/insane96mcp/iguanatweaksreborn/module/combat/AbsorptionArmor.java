package insane96mcp.iguanatweaksreborn.module.combat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Absorption Armor", description = "Armor gives regenerating absorption and regen absorption speed instead of armor and toughness")
@LoadFeature(module = Modules.Ids.COMBAT, enabledByDefault = false)
public class AbsorptionArmor extends Feature {

    public AbsorptionArmor(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onAttributeEvent(ItemAttributeModifierEvent event) {
        if (!this.isEnabled())
            return;

        Multimap<Attribute, AttributeModifier> toAdd = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> toRemove = HashMultimap.create();
        event.getModifiers().forEach((attribute, modifier) -> {
            if (attribute.equals(Attributes.ARMOR)) {
                toAdd.put(RegeneratingAbsorption.ATTRIBUTE.get(), new AttributeModifier(modifier.getId(), modifier.getName(), modifier.getAmount(), modifier.getOperation()));
                toRemove.put(attribute, modifier);
            }
            if (attribute.equals(Attributes.ARMOR_TOUGHNESS)) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADDITION)
                    toAdd.put(RegeneratingAbsorption.SPEED_ATTRIBUTE.get(), new AttributeModifier(modifier.getId(), modifier.getName(), modifier.getAmount() * 0.1d, AttributeModifier.Operation.MULTIPLY_BASE));
                else
                    toAdd.put(RegeneratingAbsorption.SPEED_ATTRIBUTE.get(), new AttributeModifier(modifier.getId(), modifier.getName(), modifier.getAmount(), modifier.getOperation()));
                toRemove.put(attribute, modifier);
            }
        });
        toRemove.forEach(event::removeModifier);
        toAdd.forEach(event::addModifier);
    }
}
