package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;

@Label(name = "Data Packs", description = "Various data packs that can be enabled/disabled")
@LoadFeature(module = Modules.Ids.MISC)
public class DataPacks extends Feature {

    //Furnace requires copper, you get nuggets by smelting raw copper on campfire
    @Config
    @Label(name = "Hardcore Torches", description = """
            Changes vanilla torch recipes.
            * Torches can be only made on Campfires early on in the game
            * With shears you can make 2 torches out of coal
            * With Fire Charges you can make them later in the game.""")
    public static Boolean hardcoreTorches = true;

    @Config
    @Label(name = "Cheaper Chains", description = "Changes vanilla chains recipe. Makes chains easily craftable with nuggets only.")
    public static Boolean cheaperChains = true;

    @Config
    @Label(name = "Misc tweaks", description = """
            Minor changes:
            * Shield crafting requires more materials
            * Crossbows can no longer be crafted, only be found in pillagers towers or from pillagers
            * Cakes now drop when broken and not eaten
            * Dark Prismarine is made easier
            * Clay balls can be crafted from blocks
            * Leads and Sticky Pistons can also be made with Honey
            * Iron Doors can be blasted back to Iron Ingots""")
    public static Boolean miscTweaks = true;

    @Config
    @Label(name = "Cheaper Rails", description = "Makes rails cheaper to encourage using them")
    public static Boolean cheaperRails = true;

    @Config
    @Label(name = "Actual Redstone Components", description = "Makes redstone components require redstone in their recipe.")
    public static Boolean actualRedstoneComponents = true;

    @Config
    @Label(name = "Disable Nether Roof", description = "Makes the nether 128 blocks high instead of 256, effectively disabling the \"Nether Roof\"")
    public static Boolean disableNetherRoof = true;

    @Config
    @Label(name = "Better Loot", description = "If true a datapack will be enabled that overhauls the loot")
    public static Boolean betterLoot = true;

    public DataPacks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "hardcore_torches", net.minecraft.network.chat.Component.literal("Survival Reimagined Hardcore Torches"), () -> this.isEnabled() && hardcoreTorches));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "cheaper_chains", net.minecraft.network.chat.Component.literal("Survival Reimagined Cheaper Chains"), () -> this.isEnabled() && cheaperChains));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "misc_tweaks", net.minecraft.network.chat.Component.literal("Survival Reimagined Misc Tweaks"), () -> this.isEnabled() && miscTweaks));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "cheaper_rails", net.minecraft.network.chat.Component.literal("Survival Reimagined Cheaper Rails"), () -> this.isEnabled() && cheaperRails));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "no_nether_roof", net.minecraft.network.chat.Component.literal("Survival Reimagined No Nether Roof"), () -> this.isEnabled() && disableNetherRoof));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "better_loot", net.minecraft.network.chat.Component.literal("Survival Reimagined Better Loot"), () -> this.isEnabled() && betterLoot));
        IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "actual_redstone_components", net.minecraft.network.chat.Component.literal("Survival Reimagined Actual Redstone components"), () -> this.isEnabled() && actualRedstoneComponents));
    }
}
