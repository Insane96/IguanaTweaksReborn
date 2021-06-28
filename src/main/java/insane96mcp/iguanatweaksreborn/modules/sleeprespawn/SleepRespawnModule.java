package insane96mcp.iguanatweaksreborn.modules.sleeprespawn;

import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature.PreventSleepingFeature;
import insane96mcp.iguanatweaksreborn.modules.sleeprespawn.feature.SleepingEffectsFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Sleep & Respawn")
public class SleepRespawnModule extends Module {

	public SleepingEffectsFeature sleepingEffects;
	public PreventSleepingFeature preventSleeping;

	public SleepRespawnModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		sleepingEffects = new SleepingEffectsFeature(this);
		preventSleeping = new PreventSleepingFeature(this);
		Config.builder.pop();
	}

	@Override
    public void loadConfig() {
		super.loadConfig();
		sleepingEffects.loadConfig();
		preventSleeping.loadConfig();
	}
}
