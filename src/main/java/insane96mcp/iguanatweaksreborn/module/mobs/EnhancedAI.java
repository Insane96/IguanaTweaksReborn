package insane96mcp.iguanatweaksreborn.module.mobs;

import insane96mcp.enhancedai.modules.creeper.CreeperSwell;
import insane96mcp.enhancedai.modules.mobs.movement.Movement;
import insane96mcp.enhancedai.modules.skeleton.SkeletonFleeTarget;
import insane96mcp.enhancedai.modules.skeleton.shoot.SkeletonShoot;
import insane96mcp.enhancedai.modules.witch.ThirstyWitches;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "EnhancedAI", description = "Changes to EnhancedAI config")
@LoadFeature(module = Modules.Ids.MOBS)
public class EnhancedAI extends Feature {

    public EnhancedAI(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);

        Module.getFeature(CreeperSwell.class).setConfigOption("Walking Fuse Chance", 0.75d);
        Module.getFeature(CreeperSwell.class).setConfigOption("Breach Chance", 0.4d);
        Module.getFeature(CreeperSwell.class).setConfigOption("Angry Creeper.Explosion power", 3.0d);
        Module.getFeature(CreeperSwell.class).setConfigOption("Angry Creeper.Forced Explosion", false);
        Module.getFeature(CreeperSwell.class).readConfig(event);

        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Near", 1.1d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee speed Multiplier Far", 1d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee Distance Near", 6d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Flee Distance Far", 13d);
        Module.getFeature(SkeletonFleeTarget.class).setConfigOption("Avoid Player chance", 0.25d);
        Module.getFeature(SkeletonFleeTarget.class).readConfig(event);

        Module.getFeature(SkeletonShoot.class).setConfigOption("Spammer chance", 0d);
        //Base 24, 40 in Hard Mode via MPR
        Module.getFeature(SkeletonShoot.class).setConfigOption("Shooting Range", new MinMax(24));
        Module.getFeature(SkeletonShoot.class).readConfig(event);

        //Controlled via MPR
        Module.getFeature(Movement.class).setConfigOption("Bonus movement speed", 0d);
        Module.getFeature(Movement.class).setConfigOption("Swim Speed Addition Multiplier", 0d);
        Module.getFeature(Movement.class).readConfig(event);

        Module.getFeature(ThirstyWitches.class).setConfigOption("Strong healing Threshold", 0.25d);
        Module.getFeature(ThirstyWitches.class).setConfigOption("Water Breathing Chance", 0.15d);
        Module.getFeature(ThirstyWitches.class).setConfigOption("Fire Resistance Chance", 0.15d);
        Module.getFeature(ThirstyWitches.class).readConfig(event);
    }
}
