package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ITModule {
    private final String name;
    private final String description;
    private final ForgeConfigSpec.ConfigValue<Boolean> enabledConfig;

    private boolean enabled;

    public ITModule(String name, String description, boolean enabledByDefault) {
        this.name = name;
        this.description = description;

        enabledConfig = Config.builder.comment(this.description).define("Enable " + this.name + " module", enabledByDefault);
    }

    public ITModule(String name, String description) {
        this(name, description, true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {
        this.enabled = enabledConfig.get();
    }
}
