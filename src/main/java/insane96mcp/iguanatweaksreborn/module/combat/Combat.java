package insane96mcp.iguanatweaksreborn.module.combat;

import insane96mcp.iguanatweaksreborn.module.combat.feature.NoKnockback;
import insane96mcp.iguanatweaksreborn.module.combat.feature.Shields;
import insane96mcp.iguanatweaksreborn.module.combat.feature.Stats;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Combat")
public class Combat extends Module {

	public Stats stats;
	public NoKnockback noKnockback;
	public Shields shields;

	public Combat() {
		super(Config.builder);
		pushConfig(Config.builder);
		stats = new Stats(this);
		noKnockback = new NoKnockback(this);
		shields = new Shields(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		stats.loadConfig();
		noKnockback.loadConfig();
		shields.loadConfig();
	}

}