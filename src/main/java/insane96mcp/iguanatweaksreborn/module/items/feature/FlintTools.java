package insane96mcp.iguanatweaksreborn.module.items.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITItems;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Flint Tools", description = "Add flint tools and make wooden tools useless")
@LoadFeature(module = Modules.Ids.ITEMS)
public class FlintTools extends ITFeature {

	private static final ILItemTier FLINT_TIER = new ILItemTier(1, 33, 3.5f, 1.5f, 5, () -> Ingredient.of(Items.FLINT));

	public static final RegistryObject<Item> FLINT_SWORD = ITItems.ITEMS.register("flint_sword", () -> new SwordItem(FLINT_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> FLINT_SHOVEL = ITItems.ITEMS.register("flint_shovel", () -> new ShovelItem(FLINT_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> FLINT_PICKAXE = ITItems.ITEMS.register("flint_pickaxe", () -> new PickaxeItem(FLINT_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> FLINT_AXE = ITItems.ITEMS.register("flint_axe", () -> new AxeItem(FLINT_TIER, 7.0F, -3.2F, new Item.Properties()));
	public static final RegistryObject<Item> FLINT_HOE = ITItems.ITEMS.register("flint_hoe", () -> new HoeItem(FLINT_TIER, -1, -2.0F, new Item.Properties()));

	public FlintTools(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}