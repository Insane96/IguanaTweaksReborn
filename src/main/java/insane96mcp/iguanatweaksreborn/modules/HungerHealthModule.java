package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;

public class HungerHealthModule {

	private static boolean loadedFoodMultipliers = false;

	public static void processFoodMultipliers() {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.foodHungerMultiplier == 1.0d && ModConfig.HungerHealth.foodSaturationMultiplier == 1.0d)
			return;
		if (loadedFoodMultipliers)
			return;
		loadedFoodMultipliers = true;
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
					}				}
			}
			if (isInBlacklist)
				continue;
			if (!isInWhitelist && ModConfig.HungerHealth.blacklistAsWhitelist)
				continue;
			Food food = item.getFood();
			food.value = (int) Math.ceil((food.value * ModConfig.HungerHealth.foodHungerMultiplier) + 0.5f);
			food.saturation *= ModConfig.HungerHealth.foodSaturationMultiplier;
		}
	}

	public static void processCustomFoodValues() {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.customFoodValue.isEmpty())
			return;
		Collection<Item> items = ForgeRegistries.ITEMS.getValues();
		for (ModConfig.HungerHealth.CustomFoodValue foodValue : ModConfig.HungerHealth.customFoodValue) {
			Item item = ForgeRegistries.ITEMS.getValue(foodValue.id);
			Food food = item.getFood();
			food.value = foodValue.hunger;
			if (foodValue.saturation != 1f)
				food.saturation = foodValue.saturation;
		}
	}

	public static void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.foodHealMultiplier == 0d)
			return;
		if (!event.getItem().isFood())
			return;
		Food food = event.getItem().getItem().getFood();
		float heal = food.value * (float) ModConfig.HungerHealth.foodHealMultiplier;
		event.getEntityLiving().heal(heal);
	}

	public static void breakExaustion(BlockEvent.BreakEvent event) {
		if (!ModConfig.Modules.hungerHealth)
			return;
		if (ModConfig.HungerHealth.blockBreakExaustionMultiplier == 0d)
			return;
		ServerWorld world = (ServerWorld) event.getWorld();
		BlockState state = world.getBlockState(event.getPos());
		Block block = state.getBlock();
		ResourceLocation dimensionId = world.getDimensionKey().getLocation();
		double hardness = state.getBlockHardness(event.getWorld(), event.getPos());
		double globalHardnessMultiplier = HardnessModule.getBlockGlobalHardness(block, dimensionId);
		if (globalHardnessMultiplier != -1d)
			hardness *= globalHardnessMultiplier;
		double singleHardness = HardnessModule.getBlockSingleHardness(block, dimensionId);
		if (singleHardness != -1d)
			hardness = singleHardness;
		event.getPlayer().addExhaustion((float) (hardness * ModConfig.HungerHealth.blockBreakExaustionMultiplier) - 0.005f);
	}

	public static void debuffsOnLowStats(TickEvent.PlayerTickEvent event) {
		if (event.player.world.isRemote())
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) event.player;

		if (player.ticksExisted % 20 != 0)
			return;

		for (ModConfig.HungerHealth.Debuff debuff : ModConfig.HungerHealth.debuffs) {
			boolean pass = false;
			switch (debuff.stat) {
				case HEALTH:
					if (player.getHealth() <= debuff.max && player.getHealth() >= debuff.min)
						pass = true;
					break;

				case HUNGER:
					if (player.getFoodStats().getFoodLevel() <= debuff.max && player.getFoodStats().getFoodLevel() >= debuff.min)
						pass = true;
					break;

				default:
					break;
			}
			if (pass) {
				EffectInstance effectInstance = new EffectInstance(debuff.effect, 30, debuff.amplifier, true, true, false);
				player.addPotionEffect(effectInstance);
			}
		}
	}
}
