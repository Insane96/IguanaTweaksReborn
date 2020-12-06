package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;

public class StackSizesModule {

	private static boolean loadedItemChanges = false;
	private static boolean loadedFoodChanges = false;
	private static boolean loadedBlockChanges = false;

	public static void processFoodStackSizes() {
		if (!ModConfig.Modules.stackSizes)
			return;
		if (!ModConfig.StackSizes.foodStackReduction)
			return;
		if (loadedFoodChanges)
			return;
		Collection<Item> items = ForgeRegistries.ITEMS.getValues();
		for (Item item : items) {
			if (!item.isFood())
				continue;
			boolean isInWhitelist = false;
			boolean isInBlacklist = false;
			for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.StackSizes.blacklist) {
				if (!ModConfig.StackSizes.blacklistAsWhitelist) {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInBlacklist = true;
						break;
					}
				}
				else {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInWhitelist = true;
						break;
					}
				}
			}
			if (isInBlacklist)
				continue;
			if (!isInWhitelist && ModConfig.StackSizes.blacklistAsWhitelist)
				continue;
			Food food = item.getFood();
			double stackSize = 64d / (food.value + 1);
			stackSize *= ModConfig.StackSizes.foodStackMultiplier;
			if (stackSize < 1d)
				stackSize = 1d;
			else if (stackSize > 64d)
				stackSize = 64d;
			item.maxStackSize = (int) Math.round(stackSize);
		}
		loadedFoodChanges = true;
	}

	public static void processItemStackSizes() {
		if (!ModConfig.Modules.stackSizes)
			return;
		if (ModConfig.StackSizes.itemStackMultiplier == 1d)
			return;
		if (loadedItemChanges)
			return;
		Collection<Item> items = ForgeRegistries.ITEMS.getValues();
		for (Item item : items) {
			if (item instanceof BlockItem)
				continue;
			if (item.maxStackSize == 1)
				continue;
			boolean isInWhitelist = false;
			boolean isInBlacklist = false;
			for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.StackSizes.blacklist) {
				if (!ModConfig.StackSizes.blacklistAsWhitelist) {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInBlacklist = true;
						break;
					}
				}
				else {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInWhitelist = true;
						break;
					}
				}
			}
			if (isInBlacklist)
				continue;
			if (!isInWhitelist && ModConfig.StackSizes.blacklistAsWhitelist)
				continue;
			double stackSize = item.maxStackSize * ModConfig.StackSizes.itemStackMultiplier;
			if (stackSize < 1d)
				stackSize = 1d;
			else if (stackSize > 64d)
				stackSize = 64d;
			item.maxStackSize = (int) Math.round(stackSize);
		}
		loadedItemChanges = true;
	}

	public static void processBlockStackSizes() {
		if (!ModConfig.Modules.stackSizes)
			return;
		if (!ModConfig.StackSizes.blockStackReduction)
			return;
		if (loadedBlockChanges)
			return;
		Collection<Item> items = ForgeRegistries.ITEMS.getValues();
		for (Item item : items) {
			if (!(item instanceof BlockItem))
				continue;
			boolean isInWhitelist = false;
			boolean isInBlacklist = false;
			for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.StackSizes.blacklist) {
				if (!ModConfig.StackSizes.blacklistAsWhitelist) {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInBlacklist = true;
						break;
					}
				}
				else {
					if (Utils.isInTagOrItem(blacklistEntry, item, null)) {
						isInWhitelist = true;
						break;
					}
				}
			}
			if (isInBlacklist)
				continue;
			if (!isInWhitelist && ModConfig.StackSizes.blacklistAsWhitelist)
				continue;
			Block block = ((BlockItem) item).getBlock();
			double weight = getBlockWeight(block.getDefaultState());
			double stackSize = item.maxStackSize / weight;
			if (stackSize < 1d)
				stackSize = 1d;
			else if (stackSize > 64d)
				stackSize = 64d;
			item.maxStackSize = (int) Math.round(stackSize);
		}
		loadedBlockChanges = true;
	}

	public static void processCustomStackSizes() {
		if (!ModConfig.Modules.stackSizes)
			return;
		if (ModConfig.StackSizes.customStackList.isEmpty())
			return;
		for (ModConfig.StackSizes.CustomStackSize customStackSize : ModConfig.StackSizes.customStackList) {
			if (customStackSize.tag != null) {
				ITag<Item> tag = ItemTags.getCollection().get(customStackSize.tag);
				if (tag == null)
					return;
				tag.getAllElements().forEach(item -> {
					item.maxStackSize = customStackSize.stackSize;
					if (item.maxStackSize <= 0)
						item.maxStackSize = 1;
					else if (item.maxStackSize > 64)
						item.maxStackSize = 64;
				});
			}
			else if (customStackSize.id != null) {
				Item item = ForgeRegistries.ITEMS.getValue(customStackSize.id);
				item.maxStackSize = customStackSize.stackSize;
				if (item.maxStackSize < 1)
					item.maxStackSize = 1;
				else if (item.maxStackSize > 64)
					item.maxStackSize = 64;
			}
		}
		loadedFoodChanges = true;
	}

	public static void fixStackedSoupsEating(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			ItemStack original = event.getItem();
			ItemStack result = event.getResultStack();
			if (original.getCount() > 1 && (result.getItem() == Items.BOWL || result.getItem() == Items.BUCKET)) {
				ItemStack newResult = original.copy();
				newResult.setCount(original.getCount() - 1);
				event.setResultStack(newResult);
				player.addItemStackToInventory(result);
			}
		}
	}

	//TODO Put this in it's own class
	public static double getBlockWeight(BlockState state) {
		Material blockMaterial = state.getMaterial();

		if (!materialWeight.containsKey(blockMaterial))
			return 1d;

		return materialWeight.get(blockMaterial);
	}

	public static HashMap<Material, Double> materialWeight = new HashMap<>();

	public static void initMaterialWeight() {
		materialWeight.put(Material.ANVIL, 6d);
		materialWeight.put(Material.BAMBOO, 2d);
		materialWeight.put(Material.BAMBOO_SAPLING, 2d);
		materialWeight.put(Material.BARRIER, 1d);
		materialWeight.put(Material.CACTUS, 2d);
		materialWeight.put(Material.CAKE, 2d);
		materialWeight.put(Material.CARPET, 2d);
		materialWeight.put(Material.CLAY, 3d);
		materialWeight.put(Material.CORAL, 2d);
		materialWeight.put(Material.DRAGON_EGG, 1d);
		materialWeight.put(Material.EARTH, 3d);
		materialWeight.put(Material.GLASS, 2d);
		materialWeight.put(Material.GOURD, 2d);
		materialWeight.put(Material.ICE, 3d);
		materialWeight.put(Material.IRON, 6d);
		materialWeight.put(Material.LEAVES, 2d);
		materialWeight.put(Material.MISCELLANEOUS, 2d);
		materialWeight.put(Material.NETHER_PLANTS, 2d);
		materialWeight.put(Material.NETHER_WOOD, 3d);
		materialWeight.put(Material.OCEAN_PLANT, 2d);
		materialWeight.put(Material.ORGANIC, 3d);
		materialWeight.put(Material.PACKED_ICE, 4d);
		materialWeight.put(Material.PISTON, 2d);
		materialWeight.put(Material.PLANTS, 2d);
		materialWeight.put(Material.REDSTONE_LIGHT, 2d);
		materialWeight.put(Material.ROCK, 4d);
		materialWeight.put(Material.SAND, 2d);
		materialWeight.put(Material.SEA_GRASS, 2d);
		materialWeight.put(Material.SHULKER, 1d);
		materialWeight.put(Material.SNOW, 2d);
		materialWeight.put(Material.SNOW_BLOCK, 3d);
		materialWeight.put(Material.SPONGE, 2d);
		materialWeight.put(Material.TALL_PLANTS, 2d);
		materialWeight.put(Material.TNT, 2d);
		materialWeight.put(Material.WEB, 2d);
		materialWeight.put(Material.WOOD, 3d);
		materialWeight.put(Material.WOOL, 4d);
	}
}
