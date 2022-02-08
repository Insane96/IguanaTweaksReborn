package insane96mcp.iguanatweaksreborn.module.misc;

import insane96mcp.iguanatweaksreborn.module.misc.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Miscellaneous")
public class Misc extends Module {

	public ExplosionOverhaul explosionOverhaul;
	public ToolStats toolStats;
	public DeBuffs deBuffs;
	public TempSpawner tempSpawner;
	public VillagerNerf villagerNerf;
	public Nerf nerf;

	public Misc() {
		super(Config.builder);
		pushConfig(Config.builder);
		explosionOverhaul = new ExplosionOverhaul(this);
		toolStats = new ToolStats(this);
		deBuffs = new DeBuffs(this);
		tempSpawner = new TempSpawner(this);
		villagerNerf = new VillagerNerf(this);
		nerf = new Nerf(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		explosionOverhaul.loadConfig();
		toolStats.loadConfig();
		deBuffs.loadConfig();
		tempSpawner.loadConfig();
		villagerNerf.loadConfig();
		nerf.loadConfig();
	}
}
