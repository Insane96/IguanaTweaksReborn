package insane96mcp.survivalreimagined.module.world;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "The End")
@LoadFeature(module = Modules.Ids.WORLD)
public class TheEnd extends Feature {
    @Config
    @Label(name = "Increase End Cities", description = "If true, a data pack will be enabled that makes End Cities will be more common.")
    public static Boolean increaseEndCities = true;

    public TheEnd(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "increased_end_cities", Component.literal("Survival Reimagined Increased End Cities"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && increaseEndCities));
    }
}
