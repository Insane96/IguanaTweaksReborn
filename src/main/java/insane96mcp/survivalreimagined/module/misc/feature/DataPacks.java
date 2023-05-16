package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;

@Label(name = "Data Packs", description = "Various data packs that can be enabled/disabled")
@LoadFeature(module = Modules.Ids.MISC)
public class DataPacks extends Feature {

    @Config
    @Label(name = "Disable ALL data packs", description = "If true, no integrated datapack will be loaded")
    public static Boolean disableAllDataPacks = false;

    //Furnace requires copper, you get nuggets by smelting raw copper on campfire
    @Config
    @Label(name = "Hardcore Torches", description = """
            Changes vanilla torch recipes.
            * Torches can be only made on Campfires early on in the game
            * With shears you can make 3 torches out of coal
            * With Fire Charges you can make them later in the game.""")
    public static Boolean hardcoreTorches = true;

    @Config
    @Label(name = "Cheaper Chains", description = "Changes vanilla chains recipe. Makes chains easily craftable with nuggets only.")
    public static Boolean cheaperChains = true;

    @Config
    @Label(name = "Misc tweaks", description = """
            Minor changes:
            * Crossbows can no longer be crafted, only be found in pillagers towers or from pillagers
            * Cakes now drop when broken and not eaten
            * Dark Prismarine is made easier
            * Clay balls can be crafted from blocks
            * Leads and Sticky Pistons can also be made with Honey
            * Iron Doors can be blasted back to Iron Ingots""")
    public static Boolean miscTweaks = true;

    @Config
    @Label(name = "Actual Redstone Components", description = "Makes redstone components require redstone in their recipe.")
    public static Boolean actualRedstoneComponents = true;

    @Config
    @Label(name = "Copper Furnace", description = "If true a datapack will be enabled that makes furnaces require copper. Copper ingots can be obtained from raw copper on campfires.")
    public static Boolean copperFurnace = true;

    @Config
    @Label(name = "Disable Villages", description = "If true a datapack will be enabled that disables villages generation.")
    public static Boolean disableVillages = true;

    @Config
    @Label(name = "Fishing Loot Changes", description = "If true a datapack will be enabled that changes fishing Loot.")
    public static Boolean fishingLootChanges = true;

    @Config
    @Label(name = "Advancements", description = "If true a datapack will be enabled that reworks advancements.")
    public static Boolean advancements = true;

    public DataPacks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "hardcore_torches", net.minecraft.network.chat.Component.literal("Survival Reimagined Hardcore Torches"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && hardcoreTorches));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "cheaper_chains", net.minecraft.network.chat.Component.literal("Survival Reimagined Cheaper Chains"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && cheaperChains));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "misc_tweaks", net.minecraft.network.chat.Component.literal("Survival Reimagined Misc Tweaks"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && miscTweaks));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "actual_redstone_components", net.minecraft.network.chat.Component.literal("Survival Reimagined Actual Redstone components"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && actualRedstoneComponents));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_furnace", net.minecraft.network.chat.Component.literal("Survival Reimagined Copper Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperFurnace));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "disable_villages", net.minecraft.network.chat.Component.literal("Survival Reimagined Disable Villages"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableVillages));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "fishing_loot_changes", net.minecraft.network.chat.Component.literal("Survival Reimagined Fishing Loot Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && fishingLootChanges));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "sr_advancements", net.minecraft.network.chat.Component.literal("Survival Reimagined Advancements"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && advancements));
    }
}
