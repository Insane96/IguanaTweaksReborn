package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Label(name = "Misc", description = "Various stuff that doesn't fit in any other Feature.")
@LoadFeature(module = Modules.Ids.MISC)
public class Misc extends Feature {

    @Config
    @Label(name = "Prevent fire with resistance", description = "If true, entities will no longer be set on fire if have Fire Resistance (like bedrock edition)")
    public static Boolean preventFireWithResistance = true;

    @Config
    @Label(name = "Slower poison", description = "If true, poison will damage the player twice as slow.")
    public static Boolean slowerPoison = true;

    public Misc(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    public static boolean isFireImmune(Entity entity) {
        if (!isEnabled(Misc.class)
                || !preventFireWithResistance
                || !(entity instanceof LivingEntity livingEntity))
            return false;

       return livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE);
    }

    public static boolean isSlowerPoison() {
        return isEnabled(Misc.class) && slowerPoison;
    }
}
