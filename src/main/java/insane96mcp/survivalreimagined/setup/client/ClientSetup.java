package insane96mcp.survivalreimagined.setup.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import insane96mcp.survivalreimagined.module.farming.feature.Crops;
import insane96mcp.survivalreimagined.module.items.feature.*;
import insane96mcp.survivalreimagined.module.movement.feature.Minecarts;
import insane96mcp.survivalreimagined.module.sleeprespawn.feature.Respawn;
import insane96mcp.survivalreimagined.module.world.feature.BeegVeins;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ClientSetup {
    public static void creativeTabsBuildContents(final CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(FlintTools.AXE.get());
            event.accept(FlintTools.PICKAXE.get());
            event.accept(FlintTools.SHOVEL.get());
            event.accept(FlintTools.HOE.get());
            event.accept(CopperTools.AXE.get());
            event.accept(CopperTools.PICKAXE.get());
            event.accept(CopperTools.SHOVEL.get());
            event.accept(CopperTools.HOE.get());
            event.accept(Florpium.AXE.get());
            event.accept(Florpium.PICKAXE.get());
            event.accept(Florpium.SHOVEL.get());
            event.accept(Florpium.HOE.get());
        }
        else if (event.getTab() == CreativeModeTabs.COMBAT) {
            event.accept(FlintTools.SWORD.get());
            event.accept(FlintTools.SHIELD.get());
            event.accept(CopperTools.SWORD.get());
            event.accept(BoneClub.BONE_CLUB.get());
            event.accept(Florpium.SWORD.get());
            event.accept(ChainedCopperArmor.HELMET.get());
            event.accept(ChainedCopperArmor.CHESTPLATE.get());
            event.accept(ChainedCopperArmor.LEGGINGS.get());
            event.accept(ChainedCopperArmor.BOOTS.get());

            event.accept(Florpium.HELMET.get());
            event.accept(Florpium.CHESTPLATE.get());
            event.accept(Florpium.LEGGINGS.get());
            event.accept(Florpium.BOOTS.get());
            event.accept(Florpium.SHIELD.get());
        }
        else if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(Florpium.BLOCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Respawn.RESPAWN_OBELISK_ITEM.get());
            event.accept(Crate.BLOCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(Minecarts.NETHER_INFUSED_POWERED_RAIL_ITEM.get());
            event.accept(ExplosiveBarrel.BLOCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(Florpium.ORE_ITEM.get());
            event.accept(Florpium.DEEPSLATE_ORE_ITEM.get());
            event.accept(Crops.CARROT_SEEDS.get());
            event.accept(Crops.POTATO_SEEDS.get());
            event.accept(BeegVeins.COPPER_ORE_ROCK_ITEM.get());
            event.accept(BeegVeins.IRON_ORE_ROCK_ITEM.get());
            event.accept(BeegVeins.GOLD_ORE_ROCK_ITEM.get());
        }
        else if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(Florpium.INGOT.get());
            event.accept(Florpium.NUGGET.get());
            event.accept(AncientLapis.ANCIENT_LAPIS.get());
            event.accept(EnchantmentsFeature.CLEANSED_LAPIS.get());
        }
    }

    public static void init() {
        DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Minecraft.getInstance().getLocale());
        SurvivalReimagined.ONE_DECIMAL_FORMATTER = new DecimalFormat("#.#", DECIMAL_FORMAT_SYMBOLS);
    }
}
