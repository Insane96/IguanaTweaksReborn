package insane96mcp.iguanatweaksreborn.module.mining;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "Materials Data Packs", description = "Various changes for different materials.")
@LoadFeature(module = Modules.Ids.MINING)
public class MaterialsDataPacks extends Feature {
	@Config
	@Label(name = "Different Coal generation", description = "If enabled, a data pack will be enabled that changes coal generation to be rarer but with bigger veins")
	public static Boolean differentCoalGeneration = true;
	@Config
	@Label(name = "Farmable Iron data pack", description = """
			Enables the following changes to vanilla data pack:
			* Stone (Broken with a non Silk-Touch tool) can drop Iron Nuggets
			* Silverfish can drop Iron Nuggets""")
	public static Boolean farmableIronDataPack = true;

	@Config
	@Label(name = "Ore Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting copper in a furnace takes 2x time
			* Smelting Iron in a Furnace takes 4x time, and 2x time in a blast furnace
			* Can no longer smelt gold and Ancient Debris in a Furnace, and 2x in a blast furnace""")
	public static Boolean oreSmelting = true;
	@Config
	@Label(name = "Backport 1(dot)20(dot)2 Diamond ore generation", description = "Enables a Data Pack.")
	public static Boolean backportDiamondGen = true;

	public MaterialsDataPacks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "farmable_iron", Component.literal("IguanaTweaks Reborn Farmable Iron"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && farmableIronDataPack));
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "ore_smelting", Component.literal("IguanaTweaks Reborn Ore Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && oreSmelting));
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "coal_generation", Component.literal("IguanaTweaks Reborn Coal Generation"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && differentCoalGeneration));
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "diamond_generation", Component.literal("IguanaTweaks Reborn Diamond Generation"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && backportDiamondGen));
	}
}
