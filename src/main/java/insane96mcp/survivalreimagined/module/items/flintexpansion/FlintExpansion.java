package insane96mcp.survivalreimagined.module.items.flintexpansion;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.item.ILItemTier;
import insane96mcp.shieldsplus.world.item.SPShieldItem;
import insane96mcp.shieldsplus.world.item.SPShieldMaterial;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Flint Expansion", description = "Add flint tools and make wooden tools useless. Also add flint blocks.")
@LoadFeature(module = Modules.Ids.ITEMS)
public class FlintExpansion extends Feature {

	public static final SPShieldMaterial SHIELD_MATERIAL = new SPShieldMaterial("flint", 2d, 33, () -> Items.FLINT, 9, Rarity.COMMON);

	public static final SimpleBlockWithItem FLINT_ROCK = SimpleBlockWithItem.register("flint_rock", () -> new GroundFlintBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 1F).offsetType(BlockBehaviour.OffsetType.XZ).dynamicShape()));

	public static final ILItemTier ITEM_TIER = new ILItemTier(1, 99, 6f, 1.5f, 9, () -> Ingredient.of(Items.FLINT));

	public static final RegistryObject<Item> SWORD = SRRegistries.ITEMS.register("flint_sword", () -> new SwordItem(ITEM_TIER, 3, -2.4F, new Item.Properties()));
	public static final RegistryObject<Item> SHOVEL = SRRegistries.ITEMS.register("flint_shovel", () -> new ShovelItem(ITEM_TIER, 1.5F, -3.0F, new Item.Properties()));
	public static final RegistryObject<Item> PICKAXE = SRRegistries.ITEMS.register("flint_pickaxe", () -> new PickaxeItem(ITEM_TIER, 1, -2.8F, new Item.Properties()));
	public static final RegistryObject<Item> AXE = SRRegistries.ITEMS.register("flint_axe", () -> new AxeItem(ITEM_TIER, 7.0F, -3.1F, new Item.Properties()));
	public static final RegistryObject<Item> HOE = SRRegistries.ITEMS.register("flint_hoe", () -> new HoeItem(ITEM_TIER, -1, -2.0F, new Item.Properties()));

	public static final RegistryObject<SPShieldItem> SHIELD = SRRegistries.registerShield("flint_shield", SHIELD_MATERIAL);

	@Config
	@Label(name = "Disable Wooden Tools + Recipes", description = "Makes wooden items deal no damage and not able to break blocks and disables the recipe.")
	public static Boolean disableWoodenToolsRecipe = false;

	public FlintExpansion(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "disable_wooden_tools", Component.literal("Survival Reimagined Disable Wooden Tools"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && disableWoodenToolsRecipe));
	}
}