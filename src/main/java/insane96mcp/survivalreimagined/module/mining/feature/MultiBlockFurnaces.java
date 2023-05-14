package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.BlockWithItem;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockBlastFurnaceBlock;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockBlastFurnaceBlockEntity;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockSoulBlastFurnaceBlock;
import insane96mcp.survivalreimagined.module.mining.block.MultiBlockSoulBlastFurnaceBlockEntity;
import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemBlastingRecipe;
import insane96mcp.survivalreimagined.module.mining.crafting.MultiItemSoulBlastingRecipe;
import insane96mcp.survivalreimagined.module.mining.data.MultiItemBlastingSerializer;
import insane96mcp.survivalreimagined.module.mining.data.MultiItemSoulBlastingSerializer;
import insane96mcp.survivalreimagined.module.mining.inventory.MultiBlockBlastFurnaceMenu;
import insane96mcp.survivalreimagined.module.mining.inventory.MultiBlockSoulBlastFurnaceMenu;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Multi Block Furnaces", description = "Add new multi block furnaces")
@LoadFeature(module = Modules.Ids.MINING)
public class MultiBlockFurnaces extends Feature {
	public static final BlockWithItem BLAST_FURNACE = BlockWithItem.register("blast_furnace", () -> new MultiBlockBlastFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
	public static final RegistryObject<BlockEntityType<MultiBlockBlastFurnaceBlockEntity>> BLAST_FURNACE_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("blast_furnace", () -> BlockEntityType.Builder.of(MultiBlockBlastFurnaceBlockEntity::new, BLAST_FURNACE.block().get()).build(null));

	public static final RegistryObject<RecipeType<MultiItemBlastingRecipe>> BLASTING_RECIPE_TYPE = SRRecipeTypes.REGISTRY.register("multi_item_blasting", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "multi_item_blasting";
		}
	});
	public static final RegistryObject<MultiItemBlastingSerializer> BLASTING_RECIPE_SERIALIZER = SRRecipeSerializers.REGISTRY.register("multi_item_blasting", MultiItemBlastingSerializer::new);
	public static final RegistryObject<MenuType<MultiBlockBlastFurnaceMenu>> BLAST_FURNACE_MENU_TYPE = SRMenuType.REGISTRY.register("blast_furnace", () -> new MenuType<>(MultiBlockBlastFurnaceMenu::new, FeatureFlags.VANILLA_SET));

	public static final BlockWithItem SOUL_BLAST_FURNACE = BlockWithItem.register("soul_blast_furnace", () -> new MultiBlockSoulBlastFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
	public static final RegistryObject<BlockEntityType<MultiBlockSoulBlastFurnaceBlockEntity>> SOUL_BLAST_FURNACE_BLOCK_ENTITY_TYPE = SRBlockEntityTypes.REGISTRY.register("soul_blast_furnace", () -> BlockEntityType.Builder.of(MultiBlockSoulBlastFurnaceBlockEntity::new, SOUL_BLAST_FURNACE.block().get()).build(null));

	public static final RegistryObject<RecipeType<MultiItemSoulBlastingRecipe>> SOUL_BLASTING_RECIPE_TYPE = SRRecipeTypes.REGISTRY.register("multi_item_soul_blasting", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "multi_item_soul_blasting";
		}
	});
	public static final RegistryObject<MultiItemSoulBlastingSerializer> SOUL_BLASTING_RECIPE_SERIALIZER = SRRecipeSerializers.REGISTRY.register("multi_item_soul_blasting", MultiItemSoulBlastingSerializer::new);
	public static final RegistryObject<MenuType<MultiBlockSoulBlastFurnaceMenu>> SOUL_BLAST_FURNACE_MENU_TYPE = SRMenuType.REGISTRY.register("soul_blast_furnace", () -> new MenuType<>(MultiBlockSoulBlastFurnaceMenu::new, FeatureFlags.VANILLA_SET));

	@Config
	@Label(name = "Blast Furnace Datapack", description = "Enables a data pack that changes the Blast Furnace recipe to give the multi block blast furnace recipe and adds Multi Block blast furnace recipes.")
	public static Boolean blastFurnaceDataPack = true;
	@Config
	@Label(name = "Soul Blast Furnace Datapack", description = "Enables a data pack that adds Multi Block soul blast furnace recipes.")
	public static Boolean soulBlastFurnaceDataPack = true;

	public MultiBlockFurnaces(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "multi_block_blast_furnace", net.minecraft.network.chat.Component.literal("Survival Reimagined Multi Block Blast Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && blastFurnaceDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "multi_block_soul_blast_furnace", net.minecraft.network.chat.Component.literal("Survival Reimagined Multi Block Soul Blast Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && soulBlastFurnaceDataPack));
	}

	@SubscribeEvent
	public void onRightClickBlastFurnace(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled()
				|| !event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(Blocks.BLAST_FURNACE))
			return;

		event.getEntity().sendSystemMessage(Component.literal("This block is not valid. Break and place it back."));
		event.setCanceled(true);
	}
}
