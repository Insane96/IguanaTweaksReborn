package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.enhancedai.modules.creeper.feature.CreeperSwell;
import insane96mcp.enhancedai.modules.skeleton.feature.SkeletonFleeTarget;
import insane96mcp.enhancedai.modules.spider.feature.ThrowingWeb;
import insane96mcp.enhancedai.modules.witch.feature.WitchFleeTarget;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "EnhancedAI", description = "Changes to EnhancedAI config")
@LoadFeature(module = Modules.Ids.MOBS)
public class EnhancedAI extends SRFeature {

    public EnhancedAI(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);

        Module.getFeature(CreeperSwell.class).setConfigOption("Cena.Chance", 0d);

        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Near", 1.1d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Far", 1d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee Distance Near", 6d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee Distance Far", 13d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Avoid Player chance", 0.25d);
        Module.getFeature(SkeletonFleeTarget.class).readConfig(event);

        Module.getFeature(ThrowingWeb.class).setConfigOption("Slowness.Amplifier", 1);
        Module.getFeature(ThrowingWeb.class).setConfigOption("Slowness.Stacking Amplifier", false);
        Module.getFeature(CreeperSwell.class).readConfig(event);


        Module.getFeature(WitchFleeTarget.class).readConfig(event);
        Module.getFeature(WitchFleeTarget.class).setConfigOption("Flee speed Multiplier Near", 1.1d);
        Module.getFeature(WitchFleeTarget.class).setConfigOption("Flee speed Multiplier Far", 1d);
    }
}
