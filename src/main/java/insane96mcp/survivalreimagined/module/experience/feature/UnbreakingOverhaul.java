package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Unbreaking overhaul", description = "Changes how Unbreaking works.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class UnbreakingOverhaul extends Feature {

	@Config
	@Label(name = "Less unbreakable unbreaking", description = "Unbreaking chance to not consume durability is changed from 50%/66.7%/75%/80%/... to 25%/45%/60%/70%/... (at levels I/II/III/IV)")
	public static Boolean tiConFormula = true;

	public UnbreakingOverhaul(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
	}

	public static boolean isTiConFormula() {
		return Feature.isEnabled(UnbreakingOverhaul.class) && tiConFormula;
	}
}