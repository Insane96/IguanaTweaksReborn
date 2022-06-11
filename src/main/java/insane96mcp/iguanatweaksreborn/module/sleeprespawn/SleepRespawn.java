package insane96mcp.iguanatweaksreborn.module.sleeprespawn;

import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.PreventSleeping;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.SleepingEffects;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature.Tiredness;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Sleep & Respawn")
public class SleepRespawn extends Module {

	public SleepingEffects sleepingEffects;
	public PreventSleeping preventSleeping;
	public Tiredness tiredness;

	public SleepRespawn() {
		super(Config.builder);
		pushConfig(Config.builder);
		sleepingEffects = new SleepingEffects(this);
		preventSleeping = new PreventSleeping(this);
		tiredness = new Tiredness(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		sleepingEffects.loadConfig();
		preventSleeping.loadConfig();
		tiredness.loadConfig();
	}
}