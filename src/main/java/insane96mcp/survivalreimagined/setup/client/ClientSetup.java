package insane96mcp.survivalreimagined.setup.client;

import insane96mcp.survivalreimagined.module.items.feature.FlintTools;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;

public class ClientSetup {
    public static void creativeTabsBuildContents(final CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(FlintTools.FLINT_AXE.get());
            event.accept(FlintTools.FLINT_PICKAXE.get());
            event.accept(FlintTools.FLINT_SHOVEL.get());
            event.accept(FlintTools.FLINT_HOE.get());
        }
        else if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(FlintTools.FLINT_SWORD.get());
        }
    }
}
