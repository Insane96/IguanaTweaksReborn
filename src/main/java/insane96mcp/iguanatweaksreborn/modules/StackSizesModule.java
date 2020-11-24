package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class StackSizesModule {

	private static boolean loadedFoodChanges = false;

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
			if (!isInWhitelist && ModConfig.Hardness.blacklistAsWhitelist)
				continue;
			Food food = item.getFood();
			double stackSize = 64d / (food.value + 1);
			stackSize *= ModConfig.StackSizes.foodStackMultiplier;
			item.maxStackSize = (int) Math.round(stackSize);
			if (item.maxStackSize <= 0)
				item.maxStackSize = 0;
			else if (item.maxStackSize > 64)
				item.maxStackSize = 64;
		}
		loadedFoodChanges = true;
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
						item.maxStackSize = 0;
					else if (item.maxStackSize > 64)
						item.maxStackSize = 64;
				});
			}
			else if (customStackSize.id != null) {
				Item item = ForgeRegistries.ITEMS.getValue(customStackSize.id);
				item.maxStackSize = customStackSize.stackSize;
				if (item.maxStackSize <= 0)
					item.maxStackSize = 0;
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
}
