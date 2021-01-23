package insane96mcp.iguanatweaksreborn.base;

import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class ITModule {
    private final String name;
    private final String description;
    private ForgeConfigSpec.ConfigValue<Boolean> enabledConfig;

    public ITModule(String name, String description) {
        this.name = name;
        this.description = description;

        enabledConfig = Config.builder.comment(this.description).define("Enable " + this.name, true);
    }

    public boolean isEnabled() {
        return enabledConfig.get();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void loadConfig() {

    }
}
