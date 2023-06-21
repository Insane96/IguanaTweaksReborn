package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Ancient Lapis", description = "Add an item that increases the level of an enchantment, up to 1 level above the limit.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class AncientLapis extends Feature {

	public AncientLapis(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}