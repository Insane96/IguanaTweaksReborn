package insane96mcp.iguanatweaksreborn.module.sleeprespawn;

import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.Sleeping;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.SleepingEffects;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.Tiredness;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Sleep & Respawn")
public class SleepRespawn extends Module {

	public SleepingEffects sleepingEffects;
	public Sleeping sleeping;
	public Tiredness tiredness;

	public SleepRespawn() {
		super(ITCommonConfig.builder);
		pushConfig(ITCommonConfig.builder);
		sleepingEffects = new SleepingEffects(this);
		sleeping = new Sleeping(this);
		tiredness = new Tiredness(this);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		sleepingEffects.loadConfig();
		sleeping.loadConfig();
		tiredness.loadConfig();
	}
}