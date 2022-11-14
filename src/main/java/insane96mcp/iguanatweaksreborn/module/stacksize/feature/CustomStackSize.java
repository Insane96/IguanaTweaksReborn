package insane96mcp.iguanatweaksreborn.module.stacksize.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.utils.IdTagValue;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.util.Mth;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Custom Stack Size", description = "Change stack sizes as you please. Changing stuff might require a Minecraft restart.")
@LoadFeature(module = Modules.Ids.STACK_SIZE)
public class CustomStackSize extends ITFeature {
    public static final List<IdTagValue> CUSTOM_STACK_LIST_DEFAULT = new ArrayList<>(Arrays.asList(
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:potion", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:minecart", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:chest_minecart", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:hopper_minecart", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:furnace_minecart", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:tnt_minecart", 16),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:snowball", 64),
            new IdTagValue(IdTagMatcher.Type.ID, "minecraft:saddle", 16)
    ));
    public static final List<IdTagValue> customStackList = new ArrayList<>();

    public CustomStackSize(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
    }

    @Override
    public void loadJsonConfigs() {
        if (!this.isEnabled())
            return;
        super.loadJsonConfigs();
        this.loadAndReadFile("custom_stack_sizes.json", customStackList, CUSTOM_STACK_LIST_DEFAULT, IdTagValue.LIST_TYPE);

        processCustomStackSizes();
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
            for (IdTagValue customStackSize : customStackList) {
                getAllItems(customStackSize).forEach(item -> item.maxStackSize = (int) Mth.clamp(customStackSize.value, 1, 64));
            }
        }
    }
}