package insane96mcp.iguanatweaksreborn.module.stacksize;

import insane96mcp.iguanatweaksreborn.module.stacksize.feature.CustomStackSize;
import insane96mcp.iguanatweaksreborn.module.stacksize.feature.GeneralStacking;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Stack Size")
public class StackSize extends Module {

	public GeneralStacking generalStacking;
	public CustomStackSize customStackSize;

	public StackSize() {
		super(Config.builder);
		pushConfig(Config.builder);
		generalStacking = new GeneralStacking(this);
		customStackSize = new CustomStackSize(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		generalStacking.loadConfig();
		customStackSize.loadConfig();
	}
}