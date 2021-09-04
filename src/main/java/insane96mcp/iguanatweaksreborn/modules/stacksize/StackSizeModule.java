package insane96mcp.iguanatweaksreborn.modules.stacksize;

import insane96mcp.iguanatweaksreborn.modules.stacksize.feature.CustomStackSizeFeature;
import insane96mcp.iguanatweaksreborn.modules.stacksize.feature.StackReductionFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;

@Label(name = "Stack Size")
public class StackSizeModule extends Module {

	public StackReductionFeature stackReduction;
	public CustomStackSizeFeature customStackSize;

	public StackSizeModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		stackReduction = new StackReductionFeature(this);
		customStackSize = new CustomStackSizeFeature(this);
		Config.builder.pop();
	}

    @Override
    public void loadConfig() {
		super.loadConfig();
		stackReduction.loadConfig();
		customStackSize.loadConfig();
	}
}
