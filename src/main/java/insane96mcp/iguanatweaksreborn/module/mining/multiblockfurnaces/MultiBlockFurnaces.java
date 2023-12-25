package insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.SimpleBlockWithItem;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockBlastFurnaceBlock;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockBlastFurnaceBlockEntity;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockSoulBlastFurnaceBlock;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.block.MultiBlockSoulBlastFurnaceBlockEntity;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting.MultiItemBlastingRecipe;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.crafting.MultiItemSoulBlastingRecipe;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.data.MultiItemBlastingSerializer;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.data.MultiItemSoulBlastingSerializer;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory.MultiBlockBlastFurnaceMenu;
import insane96mcp.iguanatweaksreborn.module.mining.multiblockfurnaces.inventory.MultiBlockSoulBlastFurnaceMenu;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
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
	public static final String INVALID_FURNACE_LANG = IguanaTweaksReborn.MOD_ID + ".invalid_blast_furnace";

	public static final SimpleBlockWithItem BLAST_FURNACE = SimpleBlockWithItem.register("blast_furnace", () -> new MultiBlockBlastFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
	public static final RegistryObject<BlockEntityType<MultiBlockBlastFurnaceBlockEntity>> BLAST_FURNACE_BLOCK_ENTITY_TYPE = SRRegistries.BLOCK_ENTITY_TYPES.register("blast_furnace", () -> BlockEntityType.Builder.of(MultiBlockBlastFurnaceBlockEntity::new, BLAST_FURNACE.block().get()).build(null));

	public static final RegistryObject<RecipeType<MultiItemBlastingRecipe>> BLASTING_RECIPE_TYPE = SRRegistries.RECIPE_TYPES.register("multi_item_blasting", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "multi_item_blasting";
		}
	});
	public static final RegistryObject<MultiItemBlastingSerializer> BLASTING_RECIPE_SERIALIZER = SRRegistries.RECIPE_SERIALIZERS.register("multi_item_blasting", MultiItemBlastingSerializer::new);
	public static final RegistryObject<MenuType<MultiBlockBlastFurnaceMenu>> BLAST_FURNACE_MENU_TYPE = SRRegistries.MENU_TYPES.register("blast_furnace", () -> new MenuType<>(MultiBlockBlastFurnaceMenu::new, FeatureFlags.VANILLA_SET));

	public static final SimpleBlockWithItem SOUL_BLAST_FURNACE = SimpleBlockWithItem.register("soul_blast_furnace", () -> new MultiBlockSoulBlastFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
	public static final RegistryObject<BlockEntityType<MultiBlockSoulBlastFurnaceBlockEntity>> SOUL_BLAST_FURNACE_BLOCK_ENTITY_TYPE = SRRegistries.BLOCK_ENTITY_TYPES.register("soul_blast_furnace", () -> BlockEntityType.Builder.of(MultiBlockSoulBlastFurnaceBlockEntity::new, SOUL_BLAST_FURNACE.block().get()).build(null));

	public static final RegistryObject<RecipeType<MultiItemSoulBlastingRecipe>> SOUL_BLASTING_RECIPE_TYPE = SRRegistries.RECIPE_TYPES.register("multi_item_soul_blasting", () -> new RecipeType<>() {
		@Override
		public String toString() {
			return "multi_item_soul_blasting";
		}
	});
	public static final RegistryObject<MultiItemSoulBlastingSerializer> SOUL_BLASTING_RECIPE_SERIALIZER = SRRegistries.RECIPE_SERIALIZERS.register("multi_item_soul_blasting", MultiItemSoulBlastingSerializer::new);
	public static final RegistryObject<MenuType<MultiBlockSoulBlastFurnaceMenu>> SOUL_BLAST_FURNACE_MENU_TYPE = SRRegistries.MENU_TYPES.register("soul_blast_furnace", () -> new MenuType<>(MultiBlockSoulBlastFurnaceMenu::new, FeatureFlags.VANILLA_SET));

	@Config
	@Label(name = "Blast Furnace Data pack", description = "Enables a data pack that changes the Blast Furnace recipe to give the multi block blast furnace recipe and adds Multi Block blast furnace recipes.")
	public static Boolean blastFurnaceDataPack = true;
	@Config
	@Label(name = "Soul Blast Furnace Data pack", description = "Enables a data pack that adds Multi Block soul blast furnace recipes.")
	public static Boolean soulBlastFurnaceDataPack = true;
	@Config
	@Label(name = "Alloying Netherite Data Pack", description = "Enables a data pack that adds a recipe to alloy netherite in a Blast or Soul furnace, requiring less materials")
	public static Boolean alloyingNetheriteDataPack = true;

	public MultiBlockFurnaces(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "multi_block_blast_furnace", Component.literal("Survival Reimagined Multi Block Blast Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && blastFurnaceDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "multi_block_soul_blast_furnace", Component.literal("Survival Reimagined Multi Block Soul Blast Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && soulBlastFurnaceDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "alloying_netherite", Component.literal("Survival Reimagined Netherite Alloy"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && alloyingNetheriteDataPack));
	}

	@SubscribeEvent
	public void onRightClickBlastFurnace(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled()
				|| !event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(Blocks.BLAST_FURNACE))
			return;

		event.getEntity().sendSystemMessage(Component.translatable(INVALID_FURNACE_LANG));
		event.setCanceled(true);
	}
}
