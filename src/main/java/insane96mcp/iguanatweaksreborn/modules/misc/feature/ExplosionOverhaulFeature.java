package insane96mcp.iguanatweaksreborn.modules.misc.feature;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
//@ITFeature(name = "Explosion Overhaul", description = "Various changes to explosions from knockback to shielding.")
public class ExplosionOverhaulFeature extends ITFeature{

    ExplosionOverhaulFeature() {
        super("Explosion Overhaul", "Various changes to explosions from knockback to shielding.", null);
    }

}
