package insane96mcp.survivalreimagined.module.mobs.feature;

import insane96mcp.enhancedai.modules.base.feature.Targeting;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.base.config.MinMax;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "Stats Buffs", description = "Increase monsters health, movement speed, etc. This feature uses Mobs Properties Randomness.")
@LoadFeature(module = Modules.Ids.MOBS)
public class StatsBuffs extends Feature {
    @Config
    @Label(name = "Enable DataPack", description = "Enables the DataPack that buffs mobs.")
    public static Boolean enableDataPack = true;

    public StatsBuffs(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "mobs_buffs", net.minecraft.network.chat.Component.literal("Survival Reimagined Mobs Stats Buffs"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && enableDataPack));
    }

    @Override
    public void readConfig(ModConfigEvent event) {
        super.readConfig(event);
        Module.getFeature(Targeting.class).setConfigOption("Follow Range Override", new MinMax(0d));
        Module.getFeature(Targeting.class).setConfigOption("XRay Range Override", new MinMax(0d));
        //Read the config values
        Module.getFeature(Targeting.class).readConfig(event);
    }
}
