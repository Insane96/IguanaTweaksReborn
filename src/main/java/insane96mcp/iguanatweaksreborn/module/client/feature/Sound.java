package insane96mcp.iguanatweaksreborn.module.client.feature;

import insane96mcp.iguanatweaksreborn.setup.ITClientConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Sound", description = "Changes to sounds")
public class Sound extends Feature {

    private final ForgeConfigSpec.BooleanValue fixShieldBlockingSoundConfig;

    public boolean fixShieldBlockingSound = true;

    public Sound(Module module) {
        super(ITClientConfig.builder, module);
        this.pushConfig(ITClientConfig.builder);
        fixShieldBlockingSoundConfig = ITClientConfig.builder
                .comment("If true the entity hit will no longer play the hurt sound if blocking with a shield.")
                .define("Fix shield blocking sound", fixShieldBlockingSound);
        ITClientConfig.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.fixShieldBlockingSound = this.fixShieldBlockingSoundConfig.get();
    }

    public boolean shouldPreventShieldSoundPlay() {
        return this.isEnabled() && this.fixShieldBlockingSound;
    }
}
