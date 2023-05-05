package insane96mcp.survivalreimagined.module.movement.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;

@Label(name = "Boats")
@LoadFeature(module = Modules.Ids.MOVEMENT, canBeDisabled = false)
public class Boats extends Feature {

	@Config
	@Label(name = "Firework rocket boats")
	public static Boolean fireworkRocketBoats = false;

	@Config
	@Label(name = "No Ice Boats", description = "If true, boats will no longer go stupidly fast on ice.")
	public static Boolean noIceBoat = true;

	public Boats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static float getBoatFriction(float glide) {
		return noIceBoat ? 0.45f : glide;
	}
}