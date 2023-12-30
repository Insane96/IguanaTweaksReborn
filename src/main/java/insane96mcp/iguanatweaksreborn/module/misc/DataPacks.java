package insane96mcp.iguanatweaksreborn.module.misc;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "Data Packs", description = "Various data packs that can be enabled/disabled")
@LoadFeature(module = Modules.Ids.MISC)
public class DataPacks extends Feature {

    @Config
    @Label(name = "Disable ALL data packs", description = "If true, no integrated data pack will be loaded")
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
            * Iron Doors can be blasted back to Iron Ingots
            * Dispensers can be made from droppers
            * Levers and glass can now be broken faster with pickaxes, cactus with hoes""")
    public static Boolean miscTweaks = true;

    @Config
    @Label(name = "Actual Redstone Components", description = "Makes redstone components require redstone in their recipe (e.g. Levers, pressure plates, etc). Also makes Corail's Woodcutter no longer able to make buttons and pressure plates.")
    public static Boolean actualRedstoneComponents = true;

    @Config
    @Label(name = "Copper Furnace", description = "If true a data pack will be enabled that makes furnaces require copper. Copper ingots can be obtained from raw copper on campfires.")
    public static Boolean copperFurnace = true;

    @Config
    @Label(name = "Disable Long Noses Structures", description = "If true a data pack will be enabled that disables villages and pillagers outpost generation.")
    public static Boolean disableLongNoses = true;

    @Config
    @Label(name = "Fishing Loot Changes", description = "If true a data pack will be enabled that changes fishing Loot.")
    public static Boolean fishingLootChanges = true;
    @Config
    @Label(name = "Increase End Cities", description = "If true, a data pack will be enabled that makes End Cities will be more common.")
    public static Boolean increaseEndCities = true;

    @Config
    @Label(name = "Better Structure Loot", description = "If true a data pack will be enabled that overhauls structure loot")
    public static Boolean betterStructureLoot = true;
    @Config
    @Label(name = "Less loot closer to spawn", description = "If true a data pack will be enabled that reduces loot from structures closer to spawn")
    public static Boolean lessLootCloserToSpawn = true;

    public DataPacks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
        super(module, enabledByDefault, canBeDisabled);
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "hardcore_torches", Component.literal("IguanaTweaks Reborn Hardcore Torches"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && hardcoreTorches));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "cheaper_chains", Component.literal("IguanaTweaks Reborn Cheaper Chains"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && cheaperChains));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "misc_tweaks", Component.literal("IguanaTweaks Reborn Misc Tweaks"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && miscTweaks));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "actual_redstone_components", Component.literal("IguanaTweaks Reborn Actual Redstone components"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && actualRedstoneComponents));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_furnace", Component.literal("IguanaTweaks Reborn Copper Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperFurnace));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "disable_long_noses", Component.literal("IguanaTweaks Reborn Disable Long Noses"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableLongNoses));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "fishing_loot_changes", Component.literal("IguanaTweaks Reborn Fishing Loot Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && fishingLootChanges));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "increased_end_cities", Component.literal("Survival Reimagined Increased End Cities"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && increaseEndCities));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "better_loot", Component.literal("Survival Reimagined Better Loot"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && betterStructureLoot));
        IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "hardcore_loot", Component.literal("IguanaTweaks Reborn Less Loot Closer to Spawn"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && lessLootCloserToSpawn));
    }
}
