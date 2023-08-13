package insane96mcp.survivalreimagined.module.sleeprespawn;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Cloth", description = "Zombies can drop cloth instead of Rotten Flesh. Cloth is used to make beds.")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class Cloth extends Feature {

	public static final RegistryObject<Item> CLOTH = SRItems.REGISTRY.register("cloth", () -> new Item(new Item.Properties()));

	@Config
	@Label(name = "Data Pack", description = "Enables a Data Pack that makes zombies drop cloth instead of rotten flesh and beds require Cloth to be crafted.")
	public static Boolean dataPack = true;

	public Cloth(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "cloth", Component.literal("Survival Reimagined Cloth"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && dataPack));
	}
}