package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.enhancedai.modules.creeper.feature.CreeperSwell;
import insane96mcp.enhancedai.modules.skeleton.feature.SkeletonFleeTarget;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "EnhancedAI", description = "Changes to EnhancedAI config")
@LoadFeature(module = Modules.Ids.MOBS)
public class EnhancedAI extends SRFeature {
    //TODO Nerf skellys
    @Config
    @Label(name = "Cena Nerf", description = "Makes creeper Cena explosion power the same as normal creeper. Cena will be just a jumpscare lul.")
    public static Boolean cenaNerf = true;

    public EnhancedAI(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);

        if (cenaNerf) {
            Module.getFeature(CreeperSwell.class).setConfigOption("Cena.Explosion power", 4d);
        }

        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Near", 1.1d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Far", 1d);
        //Read the config values
        Module.getFeature(CreeperSwell.class).readConfig(event);
    }
}
