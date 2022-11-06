package insane96mcp.iguanatweaksreborn.module.stacksize.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.stacksize.utils.ItemStackSize;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.List;

@Label(name = "Custom Stack Size Feature", description = "Change stack sizes as you please")
@LoadFeature(module = Modules.Ids.STACK_SIZE)
public class CustomStackSize extends Feature {

    private static ForgeConfigSpec.ConfigValue<List<? extends String>> customStackListConfig;
    //TODO Move to datapacks (or reloadable stuff like MobsPropertiesRandomness)?
    private static final List<String> customStackDefault = Arrays.asList("minecraft:potion,16", "minecraft:minecart,16", "minecraft:chest_minecart,16", "minecraft:hopper_minecart,16", "minecraft:furnace_minecart,16", "minecraft:tnt_minecart,16", "minecraft:snowball,64",  "minecraft:saddle,8");
    public static List<ItemStackSize> customStackList;

    public CustomStackSize(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadConfigOptions() {
        super.loadConfigOptions();
        customStackListConfig = this.getBuilder()
                .comment("Define custom item stack sizes, one string = one item/tag. Those items are not affected by other changes such as 'Food Stack Reduction'.\nThe format is modid:itemid,stack_size or #modid:tagid,stack_size\nE.g. 'minecraft:stone,16' will make stone stack up to 16.\nE.g. '#forge:stone,16' will make all the stone types stack up to 16.\nValues over 64 or lower than 1 will not work.")
                .defineList("Custom Stack Sizes", customStackDefault, o -> o instanceof String);
    }

    @Override
    public void readConfig(final ModConfigEvent event) {
        super.readConfig(event);

        customStackList = ItemStackSize.parseStringList(customStackListConfig.get());
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        processCustomStackSizes();
    }

    private final Object mutex = new Object();

    public void processCustomStackSizes() {
        if (!this.isEnabled()
            || customStackList.isEmpty())
            return;

        synchronized (mutex) {
            for (ItemStackSize customStackSize : customStackList) {
                customStackSize.getAllItems().forEach(item -> item.maxStackSize = Mth.clamp(customStackSize.stackSize, 1, 64));
            }
        }
    }
}