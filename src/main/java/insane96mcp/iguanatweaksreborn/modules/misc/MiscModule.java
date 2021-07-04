package insane96mcp.iguanatweaksreborn.modules.misc;

import insane96mcp.iguanatweaksreborn.modules.misc.feature.DebuffFeature;
import insane96mcp.iguanatweaksreborn.modules.misc.feature.ExplosionOverhaulFeature;
import insane96mcp.iguanatweaksreborn.modules.misc.feature.TempSpawnerFeature;
import insane96mcp.iguanatweaksreborn.modules.misc.feature.ToolNerfFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Miscellaneous")
public class MiscModule extends Module {

	public ExplosionOverhaulFeature explosionOverhaul;
	public DebuffFeature debuff;
	//public WeightFeature weightFeature;
	public ToolNerfFeature toolNerf;
	public TempSpawnerFeature tempSpawner;

	public MiscModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		explosionOverhaul = new ExplosionOverhaulFeature(this);
		debuff = new DebuffFeature(this);
		//weightFeature = new WeightFeature(this);
		toolNerf = new ToolNerfFeature(this);
		tempSpawner = new TempSpawnerFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		explosionOverhaul.loadConfig();
		debuff.loadConfig();
		//weightFeature.loadConfig();
		toolNerf.loadConfig();
		tempSpawner.loadConfig();
	}

}
