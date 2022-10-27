package insane96mcp.iguanatweaksreborn.module.stacksize;

import insane96mcp.iguanatweaksreborn.module.stacksize.feature.CustomStackSize;
import insane96mcp.iguanatweaksreborn.module.stacksize.feature.GeneralStacking;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Stack Size")
public class StackSize extends Module {

	public GeneralStacking generalStacking;
	public CustomStackSize customStackSize;

	public StackSize() {
		super(ITCommonConfig.builder);
		pushConfig(ITCommonConfig.builder);
		generalStacking = new GeneralStacking(this);
		customStackSize = new CustomStackSize(this);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		generalStacking.loadConfig();
		customStackSize.loadConfig();
	}
}