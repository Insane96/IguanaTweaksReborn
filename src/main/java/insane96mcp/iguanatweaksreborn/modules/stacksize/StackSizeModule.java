package insane96mcp.iguanatweaksreborn.modules.stacksize;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.stacksize.classutils.CustomStackSize;
import insane96mcp.iguanatweaksreborn.modules.stacksize.feature.FoodStackReductionFeature;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;

@Label(name = "Stack Size")
public class StackSizeModule extends ITModule {

    public FoodStackReductionFeature foodStackReductionFeature;

    public StackSizeModule() {
        super();
        Config.builder.comment(this.getDescription()).push(this.getName());
        foodStackReductionFeature = new FoodStackReductionFeature(this);
        Config.builder.pop();
    }


    public ArrayList<CustomStackSize> defaultStackSizes = new ArrayList<>();

    @Override
    public void loadConfig() {
        super.loadConfig();

        if (defaultStackSizes.isEmpty())
            defaultStackSizes = saveDefaultStackSizes();

        foodStackReductionFeature.loadConfig();
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
