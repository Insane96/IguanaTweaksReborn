package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class ITModule {
    private final ForgeConfigSpec.ConfigValue<Boolean> enabledConfig;

    private boolean enabled;

    private final String name;
    private final String description;

    public ITModule(boolean enabledByDefault) {
        if (!this.getClass().isAnnotationPresent(Label.class))
            LogHelper.Error(String.format("%s is missing the Label Annotation.", this.getClass().getName()));
        this.name = this.getClass().getAnnotation(Label.class).name();
        this.description = this.getClass().getAnnotation(Label.class).description();
        if (description.equals(""))
            enabledConfig = Config.builder.comment(this.description).define("Enable " + this.name + " module", enabledByDefault);
        else
            enabledConfig = Config.builder.define("Enable " + this.name, enabledByDefault);
    }

    public ITModule() {
        this(true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {
        this.enabled = enabledConfig.get();
    }
}
