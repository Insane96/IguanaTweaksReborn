package insane96mcp.iguanatweaksreborn.module.misc;

import insane96mcp.iguanatweaksreborn.module.misc.feature.*;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
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
	public BeaconConduit beaconConduit;

	public Misc() {
		super(ITCommonConfig.builder);
		pushConfig(ITCommonConfig.builder);
		explosionOverhaul = new ExplosionOverhaul(this);
		toolStats = new ToolStats(this);
		deBuffs = new DeBuffs(this);
		tempSpawner = new TempSpawner(this);
		villagerNerf = new VillagerNerf(this);
		nerf = new Nerf(this);
		beaconConduit = new BeaconConduit(this);
		ITCommonConfig.builder.pop();
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
		beaconConduit.loadConfig();
	}
}
