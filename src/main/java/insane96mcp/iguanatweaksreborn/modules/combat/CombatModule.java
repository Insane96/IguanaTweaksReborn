package insane96mcp.iguanatweaksreborn.modules.combat;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.combat.feature.NoItemNoKnockbackFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;

@Label(name = "Combat")
public class CombatModule extends ITModule {

	public NoItemNoKnockbackFeature noItemNoKnockback;

	public CombatModule() {
		super();
		pushConfig();
		noItemNoKnockback = new NoItemNoKnockbackFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		noItemNoKnockback.loadConfig();
	}

}
