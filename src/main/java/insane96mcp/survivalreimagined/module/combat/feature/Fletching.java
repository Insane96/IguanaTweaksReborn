package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.combat.block.SRFletchingTableBlock;
import insane96mcp.survivalreimagined.module.combat.crafting.FletchingRecipe;
import insane96mcp.survivalreimagined.module.combat.data.FletchingRecipeSerializer;
import insane96mcp.survivalreimagined.module.combat.inventory.FletchingMenu;
import insane96mcp.survivalreimagined.module.combat.item.DamageArrowItem;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
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


	public static final RegistryObject<DamageArrowItem> QUARTZ_ARROW = SRItems.REGISTRY.register("quartz_arrow", () -> new DamageArrowItem(2.5f, new Item.Properties()));
	public static final RegistryObject<DamageArrowItem> DIAMOND_ARROW = SRItems.REGISTRY.register("diamond_arrow", () -> new DamageArrowItem(3.33f, new Item.Properties()));

	@Config
	@Label(name = "Fletching Data Pack", description = """
			Enables the following changes:
			* Replaces the vanilla fletching table recipe with the mod's one
			* Adds more arrows recipes""")
	public static Boolean dataPack = true;

	public Fletching(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "fletching", Component.literal("Survival Reimagined Forging Equipment"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && dataPack));
	}
}