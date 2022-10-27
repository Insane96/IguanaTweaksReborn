package insane96mcp.iguanatweaksreborn.module.client.feature;

import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Light", description = "Changes to light")
public class Light extends Feature {

    private final ForgeConfigSpec.BooleanValue noNightVisionFlashingConfig;

    public boolean noNightVisionFlashing = true;

    public Light(Module module) {
        super(ITClientConfig.builder, module);
        this.pushConfig(ITClientConfig.builder);
        noNightVisionFlashingConfig = ITClientConfig.builder
                .comment("If true night vision will no longer flash 10 seconds before expiring, instead will slowly fade out 4 seconds before expiring.")
                .define("No Night Vision Flashing", noNightVisionFlashing);
        ITClientConfig.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.noNightVisionFlashing = this.noNightVisionFlashingConfig.get();
    }

    public boolean shouldDisableNightVisionFlashing() {
        return this.isEnabled() && this.noNightVisionFlashing;
    }
}
