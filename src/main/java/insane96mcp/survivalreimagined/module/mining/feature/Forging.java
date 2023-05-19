package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.items.feature.CopperTools;
import insane96mcp.survivalreimagined.module.items.feature.FlintExpansion;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlock;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlockEntity;
import insane96mcp.survivalreimagined.module.mining.crafting.ForgeRecipe;
import insane96mcp.survivalreimagined.module.mining.data.ForgeRecipeSerializer;
import insane96mcp.survivalreimagined.module.mining.inventory.ForgeMenu;
import insane96mcp.survivalreimagined.module.mining.item.ForgeHammerItem;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.*;
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
	public static final BlockWithItem FORGE = BlockWithItem.register("forge", () -> new ForgeBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL)));
	public static final RegistryObject<BlockEntityType<ForgeBlockEntity>> FORGE_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("forge", () -> BlockEntityType.Builder.of(ForgeBlockEntity::new, FORGE.block().get()).build(null));

	public static final RegistryObject<RecipeType<ForgeRecipe>> FORGE_RECIPE_TYPE = SRRecipeTypes.REGISTRY.register("forging", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "forging";
		}
	});
	public static final RegistryObject<ForgeRecipeSerializer> FORGE_RECIPE_SERIALIZER = SRRecipeSerializers.REGISTRY.register("forging", ForgeRecipeSerializer::new);
	public static final RegistryObject<MenuType<ForgeMenu>> FORGE_MENU_TYPE = SRMenuType.REGISTRY.register("forge", () -> new MenuType<>(ForgeMenu::new, FeatureFlags.VANILLA_SET));

	public static final RegistryObject<ForgeHammerItem> STONE_HAMMER = SRItems.REGISTRY.register("stone_hammer", () -> new ForgeHammerItem(Tiers.STONE, 40, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> FLINT_HAMMER = SRItems.REGISTRY.register("flint_hammer", () -> new ForgeHammerItem(FlintExpansion.ITEM_TIER, 28, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> COPPER_HAMMER = SRItems.REGISTRY.register("copper_hammer", () -> new ForgeHammerItem(CopperTools.ITEM_TIER, 16, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> GOLDEN_HAMMER = SRItems.REGISTRY.register("golden_hammer", () -> new ForgeHammerItem(Tiers.GOLD, 8, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> IRON_HAMMER = SRItems.REGISTRY.register("iron_hammer", () -> new ForgeHammerItem(Tiers.IRON, 32, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> DURIUM_HAMMER = SRItems.REGISTRY.register("durium_hammer", () -> new ForgeHammerItem(Durium.ITEM_TIER, 28, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> DIAMOND_HAMMER = SRItems.REGISTRY.register("diamond_hammer", () -> new ForgeHammerItem(Tiers.DIAMOND, 20, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> SOUL_STEEL_HAMMER = SRItems.REGISTRY.register("soul_steel_hammer", () -> new ForgeHammerItem(SoulSteel.ITEM_TIER, 32, new Item.Properties()));
	public static final RegistryObject<ForgeHammerItem> NETHERITE_HAMMER = SRItems.REGISTRY.register("netherite_hammer", () -> new ForgeHammerItem(Tiers.NETHERITE, 16, new Item.Properties()));
	@Config
	@Label(name = "Forging Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* All metal gear requires a forge to be made
			* Diamond Gear requires Gold gear to be forged
			* Gold Gear requires Flint/Leather gear to be forged
			* Iron Gear requires Stone / Chained Copper gear to be forged
			* Buckets and Shears require a forge to be made""")
	public static Boolean forgingEquipment = true;

	public Forging(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "forging_equipment", net.minecraft.network.chat.Component.literal("Survival Reimagined Forging Equipment"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && forgingEquipment));
	}
}
