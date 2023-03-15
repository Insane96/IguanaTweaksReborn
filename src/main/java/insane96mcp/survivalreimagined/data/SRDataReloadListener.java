package insane96mcp.survivalreimagined.data;

import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.utils.LogHelper;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SRDataReloadListener extends SimplePreparableReloadListener<Void> {
    public static final SRDataReloadListener INSTANCE;
    public static ICondition.IContext reloadContext;

    final ArrayList<SRFeature> JSON_CONFIG_FEATURES = new ArrayList<>();

    static {
        INSTANCE = new SRDataReloadListener();
    }

    @Override
    protected @NotNull Void prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        return null;
    }

    @Override
    protected void apply(@NotNull Void v, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        LogHelper.info("Reloading Survival Reimagined json data");

        for (SRFeature feature : JSON_CONFIG_FEATURES) {
            feature.loadJsonConfigs();
        }
    }

    public void registerJsonConfigFeature(SRFeature feature) {
        this.JSON_CONFIG_FEATURES.add(feature);
    }
}
