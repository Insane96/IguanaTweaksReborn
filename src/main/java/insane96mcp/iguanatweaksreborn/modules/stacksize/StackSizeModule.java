package insane96mcp.iguanatweaksreborn.modules.stacksize;

import insane96mcp.iguanatweaksreborn.modules.stacksize.classutils.CustomStackSize;
import insane96mcp.iguanatweaksreborn.modules.stacksize.feature.CustomStackSizeFeature;
import insane96mcp.iguanatweaksreborn.modules.stacksize.feature.StackReductionFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;

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

	public ArrayList<CustomStackSize> defaultStackSizes = new ArrayList<>();

    @Override
    public void loadConfig() {
		super.loadConfig();
		if (defaultStackSizes.isEmpty())
			defaultStackSizes = saveDefaultStackSizes();
		stackReduction.loadConfig();
		customStackSize.loadConfig();
	}

    private ArrayList<CustomStackSize> saveDefaultStackSizes() {
        ArrayList<CustomStackSize> defaultStackSizes = new ArrayList<>();
        Collection<Item> items = ForgeRegistries.ITEMS.getValues();
        for (Item item : items) {
            defaultStackSizes.add(new CustomStackSize(item.getRegistryName(), null, item.maxStackSize));
        }

        return defaultStackSizes;
    }
}
