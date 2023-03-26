package insane96mcp.survivalreimagined.setup.client;

import insane96mcp.survivalreimagined.module.items.feature.BoneClub;
import insane96mcp.survivalreimagined.module.items.feature.ChainedCopperArmor;
import insane96mcp.survivalreimagined.module.items.feature.FlintTools;
import insane96mcp.survivalreimagined.module.items.feature.Iridium;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;

public class ClientSetup {
    public static void creativeTabsBuildContents(final CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(FlintTools.AXE.get());
            event.accept(FlintTools.PICKAXE.get());
            event.accept(FlintTools.SHOVEL.get());
            event.accept(FlintTools.HOE.get());
            event.accept(Iridium.AXE.get());
            event.accept(Iridium.PICKAXE.get());
            event.accept(Iridium.SHOVEL.get());
            event.accept(Iridium.HOE.get());
        }
        else if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(FlintTools.SWORD.get());
            event.accept(BoneClub.BONE_CLUB.get());
            event.accept(Iridium.SWORD.get());
            event.accept(ChainedCopperArmor.HELMET.get());
            event.accept(ChainedCopperArmor.CHESTPLATE.get());
            event.accept(ChainedCopperArmor.LEGGINGS.get());
            event.accept(ChainedCopperArmor.BOOTS.get());
        }
    }
}
