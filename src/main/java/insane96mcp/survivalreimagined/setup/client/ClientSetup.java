package insane96mcp.survivalreimagined.setup.client;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Respawn;
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
            event.accept(FlintTools.SHIELD.get());
            event.accept(BoneClub.BONE_CLUB.get());
            event.accept(Iridium.SWORD.get());
            event.accept(ChainedCopperArmor.HELMET.get());
            event.accept(ChainedCopperArmor.CHESTPLATE.get());
            event.accept(ChainedCopperArmor.LEGGINGS.get());
            event.accept(ChainedCopperArmor.BOOTS.get());

            event.accept(Iridium.HELMET.get());
            event.accept(Iridium.CHESTPLATE.get());
            event.accept(Iridium.LEGGINGS.get());
            event.accept(Iridium.BOOTS.get());
            event.accept(Iridium.SHIELD.get());
        }
        else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Iridium.BLOCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Respawn.RESPAWN_OBELISK_ITEM.get());
            event.accept(Crate.BLOCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Minecarts.NETHER_INFUSED_POWERED_RAIL_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(Iridium.ORE_ITEM.get());
            event.accept(Iridium.DEEPSLATE_ORE_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(Iridium.INGOT.get());
            event.accept(Iridium.NUGGET.get());
            event.accept(AncientLapis.ANCIENT_LAPIS.get());
            event.accept(EnchantmentsFeature.CLEANSED_LAPIS.get());
        }
    }
}
