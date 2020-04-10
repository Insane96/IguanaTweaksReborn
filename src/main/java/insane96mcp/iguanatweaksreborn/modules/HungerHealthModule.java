package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class HungerHealthModule {

	private static boolean loadedFoodChanges = false;

	public static void processFoodMultipliers() {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.foodHungerMultiplier == 1.0d && ModConfig.HungerHealth.foodSaturationMultiplier == 1.0d)
			return;
		if (loadedFoodChanges)
			return;
		Collection<Item> items = ForgeRegistries.ITEMS.getValues();
		for (Item item : items) {
			if (!item.isFood())
				continue;
			boolean isInWhitelist = false;
			boolean isInBlacklist = false;
			for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.HungerHealth.blacklist) {
				if (!ModConfig.HungerHealth.blacklistAsWhitelist) {
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
			if (!isInWhitelist && ModConfig.HungerHealth.blacklistAsWhitelist)
				continue;
			Food food = item.getFood();
			food.value = (int) Math.ceil((food.value * ModConfig.HungerHealth.foodHungerMultiplier) + 0.5f);
			food.saturation *= ModConfig.HungerHealth.foodSaturationMultiplier;
		}
		loadedFoodChanges = true;
	}

	public static void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (!event.getItem().isFood())
			return;
		Food food = event.getItem().getItem().getFood();
		float heal = food.value * (float) ModConfig.HungerHealth.foodHealMultiplier;
		event.getEntityLiving().heal(heal);
	}
}
