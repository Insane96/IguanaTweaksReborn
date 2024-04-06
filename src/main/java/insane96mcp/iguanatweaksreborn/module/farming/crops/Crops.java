package insane96mcp.iguanatweaksreborn.module.farming.crops;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.data.generator.ITRItemTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.iguanatweaksreborn.setup.registry.SimpleBlockWithItem;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Label(name = "Crops", description = "Crops tweaks and less yield from crops")
@LoadFeature(module = Modules.Ids.FARMING)
public class Crops extends JsonFeature {

	public static final TagKey<Item> CHICKEN_FOOD_ITEMS = ITRItemTagsProvider.create("chicken_food_items");

	public static final TagKey<Block> HARDER_CROPS_TAG = ITRBlockTagsProvider.create("harder_crops");

	public static final RegistryObject<BlockItem> ROOTED_POTATO = ITRRegistries.ITEMS.register("rooted_potato", () -> new SeedsBlockItem(Blocks.POTATOES, new Item.Properties()));
	public static final RegistryObject<BlockItem> CARROT_SEEDS = ITRRegistries.ITEMS.register("carrot_seeds", () -> new SeedsBlockItem(Blocks.CARROTS, new Item.Properties()));
	public static final RegistryObject<WildCropBlock> WILD_WHEAT = ITRRegistries.BLOCKS.register("wild_wheat", () -> new WildCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_CARROTS = ITRRegistries.BLOCKS.register("wild_carrots", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_POTATOES = ITRRegistries.BLOCKS.register("wild_potatoes", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
	public static final RegistryObject<WildCropBlock> WILD_BEETROOTS = ITRRegistries.BLOCKS.register("wild_beetroots", () -> new WildCropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

	public static final SimpleBlockWithItem SOLANUM_NEOROSSII = SimpleBlockWithItem.register("solanum_neorossii", () -> new FlowerBlock(() -> MobEffects.MOVEMENT_SPEED, 10, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ)));
	public static final RegistryObject<Block> POTTED_SOLANUM_NEOROSSII = ITRRegistries.BLOCKS.register("potted_solanum_neorossii", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> SOLANUM_NEOROSSII.block().get(), BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)));

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
	@Label(name = "Only fully grown", description = "If the hardness should be applied to mature crops only.")
	public static Boolean onlyFullyGrown = true;

	@Config
	@Label(name = "Crops data pack", description = """
		Makes potatoes and carrots not plantable and also enables a data pack that makes the following changes:
		* Makes all vanilla crops drop only one seed (and makes carrots and potatoes drop the new seed item)
		* Makes melon seeds and pumpkin seeds harder to obtain
		* Removes carrots and potato drops from zombies
		* Removes wheat seeds from tall grass
		* Makes wild crops generate in the world
	""")
	public static Boolean dataPack = true;

	public Crops(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "crops", Component.literal("IguanaTweaks Reborn Crops"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && dataPack));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled()
				|| cropsRequireWater.equals(CropsRequireWater.NO)
				|| event.getResult().equals(Event.Result.DENY)
				|| !isAffectedByFarmland(event.getLevel(), event.getPos()))
			return;
		// Denies the growth if the crop is on farmland and the farmland is wet. If it's not on farmland the growth is not denied (e.g. Farmer's Delight rice)
		if (isCropOnFarmland(event.getLevel(), event.getPos()) && !isCropOnWetFarmland(event.getLevel(), event.getPos()))
			event.setResult(Event.Result.DENY);
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
				|| !(event.getLevel().getBlockState(event.getHitVec().getBlockPos()).getBlock() instanceof FarmBlock)
				|| !dataPack)
			return;

		if (event.getItemStack().is(Items.POTATO) || event.getItemStack().is(Items.CARROT))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void onTryToPlant(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Chicken chicken))
			return;

		chicken.goalSelector.addGoal(3, new TemptGoal(chicken, 1.0D, Ingredient.of(CARROT_SEEDS.get()), false));
	}

	@SubscribeEvent
	public void onTryToSeedChickens(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| !dataPack
				|| !(event.getTarget() instanceof Chicken chicken)
				|| !chicken.isFood(event.getItemStack())
				|| event.getItemStack().is(CHICKEN_FOOD_ITEMS))
			return;

		event.setCanceled(true);
	}

	public static void applyHardness(List<IdTagValue> list, boolean isClientSide) {
		for (IdTagValue hardness : list) {
			getAllBlocks(hardness.id, isClientSide).forEach(block -> {
				if (onlyFullyGrown) {
					//I have doubts that this always takes the fully grown modded crops
					BlockState state = block.getStateDefinition().getPossibleStates().get(block.getStateDefinition().getPossibleStates().size() - 1);
					if (state.destroySpeed == 0f)
						state.destroySpeed = (float) hardness.value;
				}
				else {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> {
						if (blockState.destroySpeed == 0f)
							blockState.destroySpeed = (float) hardness.value;
					});
				}
			});
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onCropBreaking(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| event.getState().destroySpeed == 0f
				|| !event.getState().is(HARDER_CROPS_TAG))
			return;
		ItemStack heldStack = event.getEntity().getMainHandItem();
		if (!(heldStack.getItem() instanceof TieredItem heldItem))
			return;
		if (!heldItem.canPerformAction(heldStack, ToolActions.HOE_DIG) && !heldItem.canPerformAction(heldStack, ToolActions.AXE_DIG))
			return;
		float efficiency = heldItem.getTier().getSpeed();
		if (efficiency > 1.0F) {
			int efficiencyLevel = EnchantmentHelper.getBlockEfficiency(event.getEntity());
			ItemStack itemstack = event.getEntity().getMainHandItem();
			if (efficiencyLevel > 0 && !itemstack.isEmpty()) {
				efficiency += (float) (efficiencyLevel * efficiencyLevel + 1);
			}
		}
		if (heldItem.canPerformAction(heldStack, ToolActions.HOE_DIG))
			event.setNewSpeed(event.getNewSpeed() * efficiency);
		else
			event.setNewSpeed(event.getNewSpeed() / efficiency);
	}

	public static int getWaterHydrationRadius() {
		return isEnabled(Crops.class) ? waterHydrationRadius : 4;
	}
}