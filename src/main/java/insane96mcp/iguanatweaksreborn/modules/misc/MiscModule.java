package insane96mcp.iguanatweaksreborn.modules.misc;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.misc.feature.ExplosionOverhaulFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Miscellaneous")
public class MiscModule extends ITModule {

	public ExplosionOverhaulFeature explosionOverhaulFeature;

	public MiscModule() {
		super();
		Config.builder.comment(this.getDescription()).push(this.getName());
		explosionOverhaulFeature = new ExplosionOverhaulFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		explosionOverhaulFeature.loadConfig();
	}

}
