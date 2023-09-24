package insane96mcp.survivalreimagined.module.mining.forging;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SimpleBlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.copper.CopperToolsExpansion;
import insane96mcp.survivalreimagined.module.items.flintexpansion.FlintExpansion;
import insane96mcp.survivalreimagined.module.items.solarium.Solarium;
import insane96mcp.survivalreimagined.module.mining.Durium;
import insane96mcp.survivalreimagined.module.mining.SoulSteel;
import insane96mcp.survivalreimagined.module.mining.keego.Keego;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlock;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlockEntity;
import insane96mcp.survivalreimagined.module.misc.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Forging")
@LoadFeature(module = Modules.Ids.MINING)
public class Forging extends Feature {
	public static final SimpleBlockWithItem FORGE = SimpleBlockWithItem.register("forge", () -> new ForgeBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)));
	public static final RegistryObject<BlockEntityType<ForgeBlockEntity>> FORGE_BLOCK_ENTITY_TYPE = SRRegistries.BLOCK_ENTITY_TYPES.register("forge", () -> BlockEntityType.Builder.of(ForgeBlockEntity::new, FORGE.block().get()).build(null));

	public static final RegistryObject<RecipeType<ForgeRecipe>> FORGE_RECIPE_TYPE = SRRegistries.RECIPE_TYPES.register("forging", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "forging";
		}
	});
	public static final RegistryObject<ForgeRecipeSerializer> FORGE_RECIPE_SERIALIZER = SRRegistries.RECIPE_SERIALIZERS.register("forging", ForgeRecipeSerializer::new);
	public static final RegistryObject<MenuType<ForgeMenu>> FORGE_MENU_TYPE = SRRegistries.MENU_TYPES.register("forge", () -> new MenuType<>(ForgeMenu::new, FeatureFlags.VANILLA_SET));

	public static final RegistryObject<ForgeHammerItem> WOODEN_HAMMER = SRRegistries.ITEMS.register("wooden_hammer", () -> new ForgeHammerItem(Tiers.WOOD, 40, 4, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> STONE_HAMMER = SRRegistries.ITEMS.register("stone_hammer", () -> new ForgeHammerItem(Tiers.STONE, 30, 4, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> FLINT_HAMMER = SRRegistries.ITEMS.register("flint_hammer", () -> new ForgeHammerItem(FlintExpansion.ITEM_TIER, 22, 4, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> GOLDEN_HAMMER = SRRegistries.ITEMS.register("golden_hammer", () -> new ForgeHammerItem(Tiers.GOLD, 6, 4, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> COPPER_HAMMER = SRRegistries.ITEMS.register("copper_hammer", () -> new ForgeHammerItem(CopperToolsExpansion.COPPER_ITEM_TIER, 14, 3, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> IRON_HAMMER = SRRegistries.ITEMS.register("iron_hammer", () -> new ForgeHammerItem(Tiers.IRON, 22, 3, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> SOLARIUM_HAMMER = SRRegistries.ITEMS.register("solarium_hammer", () -> new SolariumForgeHammerItem(Solarium.ITEM_TIER, 24, 3, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> DURIUM_HAMMER = SRRegistries.ITEMS.register("durium_hammer", () -> new ForgeHammerItem(Durium.ITEM_TIER, 22, 3, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> COATED_COPPER_HAMMER = SRRegistries.ITEMS.register("coated_copper_hammer", () -> new ForgeHammerItem(CopperToolsExpansion.COATED_ITEM_TIER, 10, 2, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> DIAMOND_HAMMER = SRRegistries.ITEMS.register("diamond_hammer", () -> new ForgeHammerItem(Tiers.DIAMOND, 14, 3, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> SOUL_STEEL_HAMMER = SRRegistries.ITEMS.register("soul_steel_hammer", () -> new ForgeHammerItem(SoulSteel.ITEM_TIER, 14, 2, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> KEEGO_HAMMER = SRRegistries.ITEMS.register("keego_hammer", () -> new KeegoForgeHammerItem(Keego.ITEM_TIER, 18, 2, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> NETHERITE_HAMMER = SRRegistries.ITEMS.register("netherite_hammer", () -> new ForgeHammerItem(Tiers.NETHERITE, 16, 2, new Item.Properties()));
	@Config
	@Label(name = "Forging Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* All metal gear requires a forge to be made
			* Diamond Gear requires Gold gear to be forged
			* Gold Gear requires Flint / Leather gear to be forged
			* Iron Gear requires Stone / Chained Copper gear to be forged
			* Buckets, Flint and Steel and Shears require a forge to be made""")
	public static Boolean forgingEquipment = true;

	public Forging(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "forging_equipment", Component.literal("Survival Reimagined Forging Equipment"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && forgingEquipment));
	}
}
