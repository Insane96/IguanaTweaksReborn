package insane96mcp.iguanatweaksreborn.module.farming.crops;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.iguanatweaksreborn.data.lootmodifier.ReplaceLootModifier;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import insane96mcp.iguanatweaksreborn.setup.SRRegistries;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;

@Label(name = "Crops", description = "Crops tweaks and less yield from crops")
@LoadFeature(module = Modules.Ids.FARMING)
public class Crops extends Feature {
	@Config
	@Label(name = "Crops Require Water", description = """
						Set if crops require wet farmland to grow.
						Valid Values:
						NO: Crops will not require water to grow
						BONE_MEAL_ONLY: Crops will grow on dry farmland by only using bone meal
						ANY_CASE: Will make Crops not grow in any case when on dry farmland""")
	public static CropsRequireWater cropsRequireWater = CropsRequireWater.ANY_CASE;

	@Config(min = 1)
	@Label(name = "Water Hydration Radius", description = "Radius where water hydrates farmland, vanilla is 4.")
	public static Integer waterHydrationRadius = 2;

	@Config
	@Label(name = "No Seed renew datapack", description = "Enables a datapack that makes crops only drop one seed.")
	public static Boolean noSeedRenewDatapack = true;

	public static final RegistryObject<BlockItem> ROOTED_POTATO = SRRegistries.ITEMS.register("rooted_potato", () -> new SeedsBlockItem(Blocks.POTATOES, new Item.Properties()));
	public static final RegistryObject<BlockItem> CARROT_SEEDS = SRRegistries.ITEMS.register("carrot_seeds", () -> new SeedsBlockItem(Blocks.CARROTS, new Item.Properties()));
	public static final RegistryObject<WildCropBlock> WILD_WHEAT = SRRegistries.BLOCKS.register("wild_wheat", () -> new WildCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_CARROTS = SRRegistries.BLOCKS.register("wild_carrots", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_POTATOES = SRRegistries.BLOCKS.register("wild_potatoes", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_BEETROOTS = SRRegistries.BLOCKS.register("wild_beetroots", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

	public Crops(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_seed_renew", Component.literal("Survival Reimagined No Seed Renew"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && noSeedRenewDatapack));
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| cropsRequireWater.equals(CropsRequireWater.NO)
				|| event.getResult().equals(Event.Result.DENY)
				|| !isAffectedByFarmland(event.getLevel(), event.getPos()))
			return;
		// Denies the growth if the crop is on farmland and the farmland is wet. If it's not on farmland the growth is not denied (e.g. Farmer's Delight rice)
		if (isCropOnFarmland(event.getLevel(), event.getPos()) && !isCropOnWetFarmland(event.getLevel(), event.getPos())) {
			event.setResult(Event.Result.DENY);
		}
	}

	public static boolean requiresWetFarmland(Level level, BlockPos blockPos) {
		return isEnabled(Crops.class)
				&& !cropsRequireWater.equals(CropsRequireWater.NO)
				&& isAffectedByFarmland(level, blockPos);
	}

	public static boolean hasWetFarmland(Level level, BlockPos blockPos) {
		return isCropOnFarmland(level, blockPos) && isCropOnWetFarmland(level, blockPos);
	}

	public enum CropsRequireWater {
		NO,
		BONE_MEAL_ONLY,
		ANY_CASE
	}

	/**
	 * @return true if the block is affected by the block below
	 */
	public static boolean isAffectedByFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState state = levelAccessor.getBlockState(cropPos);
		Block block = state.getBlock();
		return block instanceof CropBlock || block instanceof StemBlock;
	}

	/**
	 * @return true if the block is on wet farmland
	 */
	public static boolean isCropOnWetFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		if (!(sustainState.getBlock() instanceof FarmBlock))
			return false;
		int moisture = sustainState.getValue(FarmBlock.MOISTURE);
		return moisture >= 7;
	}


	/**
	 * @return true if the block is on farmland
	 */
	public static boolean isCropOnFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		return sustainState.getBlock() instanceof FarmBlock;
	}

	@SubscribeEvent
	public void onTryToPlant(PlayerInteractEvent.RightClickBlock event) {
		if (!this.isEnabled()
				|| !(event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock() instanceof FarmBlock))
			return;

		if (event.getItemStack().is(Items.POTATO) || event.getItemStack().is(Items.CARROT))
			event.setCanceled(true);
	}

	private static final Ingredient CHICKEN_FOOD_ITEMS = Ingredient.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS);
	@SubscribeEvent
	public void onTryToSeedChickens(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| !noSeedRenewDatapack
				|| !(event.getTarget() instanceof Chicken)
				|| !CHICKEN_FOOD_ITEMS.test(event.getItemStack()))
			return;

		event.setCanceled(true);
	}

	public static int getWaterHydrationRadius() {
		return isEnabled(Crops.class) ? waterHydrationRadius : 4;
	}

	private static final String path = "crops/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "replace_carrot_from_zombie", new ReplaceLootModifier.Builder(EntityType.ZOMBIE, Items.CARROT, Items.IRON_INGOT).build());
		provider.add(path + "replace_potato_from_zombie", new ReplaceLootModifier.Builder(EntityType.ZOMBIE, Items.POTATO, Items.IRON_INGOT).build());
		provider.add(path + "replace_baked_potato_from_zombie", new ReplaceLootModifier.Builder(EntityType.ZOMBIE, Items.BAKED_POTATO, Items.IRON_INGOT).build());
		provider.add(path + "replace_carrot_from_husk", new ReplaceLootModifier.Builder(EntityType.HUSK, Items.CARROT, Items.GOLD_INGOT).build());
		provider.add(path + "replace_potato_from_husk", new ReplaceLootModifier.Builder(EntityType.HUSK, Items.POTATO, Items.GOLD_INGOT).build());
		provider.add(path + "replace_baked_potato_from_husk", new ReplaceLootModifier.Builder(EntityType.HUSK, Items.BAKED_POTATO, Items.GOLD_INGOT).build());
		provider.add(path + "replace_iron_ingot_from_husk", new ReplaceLootModifier.Builder(EntityType.HUSK, Items.IRON_INGOT, Items.GOLD_INGOT).build());
		provider.add(path + "replace_carrot_from_zombie_villager", new ReplaceLootModifier.Builder(EntityType.ZOMBIE_VILLAGER, Items.CARROT, Items.EMERALD).build());
		provider.add(path + "replace_potato_from_zombie_villager", new ReplaceLootModifier.Builder(EntityType.ZOMBIE_VILLAGER, Items.POTATO, Items.EMERALD).build());
		provider.add(path + "replace_baked_potato_from_zombie_villager", new ReplaceLootModifier.Builder(EntityType.ZOMBIE_VILLAGER, Items.BAKED_POTATO, Items.EMERALD).build());
		provider.add(path + "replace_iron_ingot_from_zombie_villager", new ReplaceLootModifier.Builder(EntityType.ZOMBIE_VILLAGER, Items.IRON_INGOT, Items.EMERALD).build());

		provider.add(path + "no_seeds_from_grass", new DropMultiplierModifier.Builder(Blocks.GRASS, Items.WHEAT_SEEDS, 0f).build());
		provider.add(path + "no_seeds_from_tall_grass", new DropMultiplierModifier.Builder(Blocks.TALL_GRASS, Items.WHEAT_SEEDS, 0f).build());
		provider.add(path + "no_seeds_from_fern", new DropMultiplierModifier.Builder(Blocks.FERN, Items.WHEAT_SEEDS, 0f).build());
		provider.add(path + "no_seeds_from_large_fern", new DropMultiplierModifier.Builder(Blocks.LARGE_FERN, Items.WHEAT_SEEDS, 0f).build());
	}


	@SubscribeEvent
	public void missingMappings(MissingMappingsEvent event) {
		if (event.getMappings(ForgeRegistries.Keys.ITEMS, IguanaTweaksReborn.MOD_ID).isEmpty())
			return;

		event.getMappings(ForgeRegistries.Keys.ITEMS, IguanaTweaksReborn.MOD_ID).forEach(blockMapping -> {
            if (blockMapping.getKey().toString().equals("iguanatweaksreborn:potato_seeds"))
                blockMapping.remap(ROOTED_POTATO.get());
		});
	}
}