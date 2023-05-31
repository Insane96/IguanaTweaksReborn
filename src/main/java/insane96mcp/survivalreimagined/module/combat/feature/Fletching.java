package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.combat.block.SRFletchingTableBlock;
import insane96mcp.survivalreimagined.module.combat.crafting.FletchingRecipe;
import insane96mcp.survivalreimagined.module.combat.data.FletchingRecipeSerializer;
import insane96mcp.survivalreimagined.module.combat.inventory.FletchingMenu;
import insane96mcp.survivalreimagined.setup.SRMenuType;
import insane96mcp.survivalreimagined.setup.SRRecipeSerializers;
import insane96mcp.survivalreimagined.setup.SRRecipeTypes;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Fletching table", description = "Gives a use to the fletching table.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class Fletching extends Feature {
	public static final BlockWithItem FLETCHING_TABLE = BlockWithItem.register("fletching_table", () -> new SRFletchingTableBlock(BlockBehaviour.Properties.copy(Blocks.FLETCHING_TABLE)));

	public static final RegistryObject<RecipeType<FletchingRecipe>> FLETCHING_RECIPE_TYPE = SRRecipeTypes.REGISTRY.register("fletching", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "fletching";
		}
	});
	public static final RegistryObject<FletchingRecipeSerializer> FLETCHING_RECIPE_SERIALIZER = SRRecipeSerializers.REGISTRY.register("fletching", FletchingRecipeSerializer::new);
	public static final RegistryObject<MenuType<FletchingMenu>> FLETCHING_MENU_TYPE = SRMenuType.REGISTRY.register("fletching", () -> new MenuType<>(FletchingMenu::new, FeatureFlags.VANILLA_SET));

	public Fletching(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}
}