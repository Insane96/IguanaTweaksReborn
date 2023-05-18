package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlock;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlockEntity;
import insane96mcp.survivalreimagined.module.mining.crafting.ForgeRecipe;
import insane96mcp.survivalreimagined.module.mining.data.ForgeRecipeSerializer;
import insane96mcp.survivalreimagined.module.mining.inventory.ForgeMenu;
import insane96mcp.survivalreimagined.setup.SRBlockEntityTypes;
import insane96mcp.survivalreimagined.setup.SRMenuType;
import insane96mcp.survivalreimagined.setup.SRRecipeSerializers;
import insane96mcp.survivalreimagined.setup.SRRecipeTypes;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
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

	public Forging(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}
