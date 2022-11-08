package insane96mcp.iguanatweaksreborn.data;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ITDataReloadListener extends SimplePreparableReloadListener<Void> {
    public static final ITDataReloadListener INSTANCE;

    final ArrayList<ITFeature> JSON_CONFIG_FEATURES = new ArrayList<>();

    static {
        INSTANCE = new ITDataReloadListener();
    }

    @Override
    protected @NotNull Void prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        return null;
    }

    @Override
    protected void apply(@NotNull Void v, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        LogHelper.info("Reloading IguanaTweaks Reborn json data");

        for (ITFeature feature : JSON_CONFIG_FEATURES) {
            feature.loadJsonConfigs();
        }
    }

    public void registerJsonConfigFeature(ITFeature feature) {
        this.JSON_CONFIG_FEATURES.add(feature);
    }
}
