package insane96mcp.iguanatweaksreborn.modules.sleeprespawn;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature.PreventSleepingFeature;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature.SleepingEffectsFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Sleep & Respawn")
public class SleepRespawnModule extends ITModule {

    public SleepingEffectsFeature sleepingEffectsFeature;
    public PreventSleepingFeature preventSleepingFeature;

    public SleepRespawnModule() {
        super();
        pushConfig();
        sleepingEffectsFeature = new SleepingEffectsFeature(this);
        preventSleepingFeature = new PreventSleepingFeature(this);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        sleepingEffectsFeature.loadConfig();
        preventSleepingFeature.loadConfig();
    }
}
