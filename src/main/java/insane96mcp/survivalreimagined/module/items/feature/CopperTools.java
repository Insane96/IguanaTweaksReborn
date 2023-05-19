package insane96mcp.survivalreimagined.module.items.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Copper Tools", description = "Upgrade for flint tools")
@LoadFeature(module = Modules.Ids.ITEMS)
public class CopperTools extends Feature {

	public static final ILItemTier ITEM_TIER = new ILItemTier(2, 143, 8f, 1.0f, 9, () -> Ingredient.of(Items.COPPER_INGOT));

	public static final RegistryObject<Item> SWORD = SRItems.REGISTRY.register("copper_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRItems.REGISTRY.register("copper_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRItems.REGISTRY.register("copper_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRItems.REGISTRY.register("copper_axe", () -> new AxeItem(ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRItems.REGISTRY.register("copper_hoe", () -> new HoeItem(ITEM_TIER, -1, -2.0F, new Item.Properties()));

	@Config
	@Label(name = "Enable crafting recipes in anvil", description = "Disable copper tools recipes.")
	public static Boolean enableCopperToolsRecipes = true;

	public CopperTools(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_tools_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Copper Tools Crafting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && enableCopperToolsRecipes));
	}
}