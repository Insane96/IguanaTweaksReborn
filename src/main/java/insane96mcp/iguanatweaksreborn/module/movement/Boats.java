package insane96mcp.iguanatweaksreborn.module.movement;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.LoadFeature;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;

@Label(name = "Boats")
@LoadFeature(module = Modules.Ids.MOVEMENT)
public class Boats extends Feature {

	@Config
	@Label(name = "No Ice Boats", description = "If true, boats will no longer go stupidly fast on ice. (If quark is present this is disabled)")
	public static Boolean noIceBoat = true;
	@Config(min = 0d)
	@Label(name = "Break height", description = "If true, boats will always break when falling from this height or more")
	public static Double breakHeight = 5d;

	public Boats(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static float getBoatFriction(float glide) {
		return Feature.isEnabled(Boats.class) && noIceBoat ? 0.45f : glide;
	}
}