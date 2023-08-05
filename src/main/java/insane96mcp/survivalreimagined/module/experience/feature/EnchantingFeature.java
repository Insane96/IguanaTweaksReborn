package insane96mcp.survivalreimagined.module.experience.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.experience.integration.EnchantingInfuser;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Enchanting", description = "Change some enchanting related stuff.")
@LoadFeature(module = Modules.Ids.EXPERIENCE)
public class EnchantingFeature extends SRFeature {

	public static final RegistryObject<Item> PARCHMENT = SRItems.REGISTRY.register("parchment", () -> new Item(new Item.Properties().stacksTo(1)));
	@Config
	@Label(name = "Nether Enchanting Table", description = "Enables a data pack that makes the enchanting table require nether access to be made")
	public static Boolean netherEnchantingTable = true;
	@Config
	@Label(name = "Enchanting Infuser Compat", description = "Enables a data pack that changes the Enchanting Infuser recipe to require End access and changes some config values of the mod")
	public static Boolean enchantingInfuserCompat = true;

	public EnchantingFeature(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "nether_enchanting_table", Component.literal("Survival Reimagined Nether Enchanting Table"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && netherEnchantingTable));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "enchanting_infuser_compat", Component.literal("Survival Reimagined Enchanting Infuser Compat"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && enchantingInfuserCompat));
	}

	@SubscribeEvent
	public void onServerStarted(ServerStartedEvent event) {
		if (enchantingInfuserCompat && ModList.get().isLoaded("enchantinginfuser"))
			EnchantingInfuser.setConfigOptions();
	}
}