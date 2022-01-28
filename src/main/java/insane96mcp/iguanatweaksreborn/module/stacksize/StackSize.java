package insane96mcp.iguanatweaksreborn.module.stacksize;

import insane96mcp.iguanatweaksreborn.module.stacksize.feature.CustomStackSize;
import insane96mcp.iguanatweaksreborn.module.stacksize.feature.StackReduction;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Stack Size")
public class StackSize extends Module {

	public StackReduction stackReduction;
	public CustomStackSize customStackSize;

	public StackSize() {
		super(Config.builder);
		pushConfig(Config.builder);
		stackReduction = new StackReduction(this);
		customStackSize = new CustomStackSize(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		stackReduction.loadConfig();
		customStackSize.loadConfig();
	}
}