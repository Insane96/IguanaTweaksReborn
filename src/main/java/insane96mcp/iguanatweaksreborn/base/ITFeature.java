package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.ITDataReloadListener;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Module;

import java.io.File;

public class ITFeature extends Feature {
    public ITFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        jsonConfigFolder = new File(IguanaTweaksReborn.CONFIG_FOLDER + "/" + module.getName() + "/" + this.getName());
        if (!jsonConfigFolder.exists()) {
            if (!jsonConfigFolder.mkdir()) {
                LogHelper.warn("Failed to create %s json config folder", this.getName());
            }
        }
        ITDataReloadListener.INSTANCE.registerJsonConfigFeature(this);
    }

    protected final File jsonConfigFolder;

    public void loadJsonConfigs() {

    }
}
