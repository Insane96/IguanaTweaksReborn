package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Actually Sweet Berry Bushes", description = "Berry bushes no longer deal damage when walking in them with leggings and boots.")
@LoadFeature(module = Modules.Ids.WORLD)
public class ActuallySweetBerryBushes extends Feature {

    public ActuallySweetBerryBushes(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @SubscribeEvent
    public void onBushesDamage(LivingDamageEvent event) {
        if (!this.isEnabled()
            || !event.getSource().is(DamageTypes.SWEET_BERRY_BUSH))
            return;

        if (!event.getEntity().getItemBySlot(EquipmentSlot.LEGS).isEmpty() && !event.getEntity().getItemBySlot(EquipmentSlot.FEET).isEmpty())
            event.setCanceled(true);
    }
}
