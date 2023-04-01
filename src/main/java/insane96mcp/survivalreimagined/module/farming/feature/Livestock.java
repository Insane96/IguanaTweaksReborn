package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.farming.utils.LivestockData;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import sereneseasons.api.season.Season;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Livestock", description = "Slower breeding, Growing, Egging and Milking. Lower yield.")
@LoadFeature(module = Modules.Ids.FARMING)
public class Livestock extends SRFeature {

	public static final String MILK_COOLDOWN_TRANSLATION = SurvivalReimagined.MOD_ID + ".milk_cooldown";
	public static final String MILK_COOLDOWN = SurvivalReimagined.RESOURCE_PREFIX + "milk_cooldown";

	public static final ArrayList<LivestockData> GROWTH_SLOWNDOWN_DEFAULT = new ArrayList<>(List.of(
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 2.5d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 2.0d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 3.0d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 4.0d, Season.WINTER),

			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 2.5d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 2.0d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 3.0d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 4.0d, Season.WINTER)
	));

	public static final ArrayList<LivestockData> growthSlowdown = new ArrayList<>();

	public static final ArrayList<LivestockData> BREEDING_COOLDOWN_DEFAULT = new ArrayList<>(List.of(
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 2.5d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 2.0d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 3.0d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 4.0d, Season.WINTER),

			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 5d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 4.0d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 6.0d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 8.0d, Season.WINTER)
	));

	public static final ArrayList<LivestockData> breedingCooldown = new ArrayList<>();

	public static final ArrayList<LivestockData> EGG_LAY_SLOWDOWN_DEFAULT = new ArrayList<>(List.of(
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "chickens", 3.0d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "chickens", 2.5d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "chickens", 3.5d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "chickens", 5.0d, Season.WINTER)
	));

	public static final ArrayList<LivestockData> eggLaySlowdown = new ArrayList<>();

	public static final ArrayList<LivestockData> COW_MILK_COOLDOWN_DEFAULT = new ArrayList<>(List.of(
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "cows", 1500, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "cows", 1200, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "cows", 1800, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "cows", 3000, Season.WINTER)
	));

	public static final ArrayList<LivestockData> cowMilkCooldown = new ArrayList<>();

	public static final ArrayList<LivestockData> BREEDING_FAIL_CHANCE_DEFAULT = new ArrayList<>(List.of(
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 0.6d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 0.5d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 0.7d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, SurvivalReimagined.RESOURCE_PREFIX + "breedable_animals", 0.9d, Season.WINTER),

			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 0.6d, Season.SPRING),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 0.5d, Season.SUMMER),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 0.7d, Season.AUTUMN),
			new LivestockData(IdTagMatcher.Type.TAG, "minecraft:villager", 0.9d, Season.WINTER)
	));

	public static final ArrayList<LivestockData> breedingFailChance = new ArrayList<>();

	public Livestock(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("growth_slowdown_multiplier.json", growthSlowdown, GROWTH_SLOWNDOWN_DEFAULT, LivestockData.livestockDataListType);
		this.loadAndReadFile("breeding_cooldown_multiplier.json", breedingCooldown, BREEDING_COOLDOWN_DEFAULT, LivestockData.livestockDataListType);
		this.loadAndReadFile("egg_lay_cooldown_multiplier.json", eggLaySlowdown, EGG_LAY_SLOWDOWN_DEFAULT, LivestockData.livestockDataListType);
		this.loadAndReadFile("cow_milk_cooldown.json", cowMilkCooldown, COW_MILK_COOLDOWN_DEFAULT, LivestockData.livestockDataListType);
		this.loadAndReadFile("breeding_fail_chance.json", breedingFailChance, BREEDING_FAIL_CHANCE_DEFAULT, LivestockData.livestockDataListType);
	}

	/*@SubscribeEvent
	public void onSheepSpawn(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Sheep sheep))
			return;

		sheep.goalSelector.removeGoal(sheep.eatBlockGoal);
		sheep.eatBlockGoal = new SREatBlockGoal(sheep);
		sheep.goalSelector.addGoal(5, sheep.eatBlockGoal);
	}*/

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;

		slowdownAnimalGrowth(event);
		slowdownBreeding(event);
		slowdownEggLay(event);
		cowMilkTick(event);
	}

	public void slowdownAnimalGrowth(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof AgeableMob mob))
			return;

		int growingAge = mob.getAge();
		if (growingAge >= 0)
			return;

		double multiplier = 0d;
		for (LivestockData data : growthSlowdown){
			if (data.matches(mob))
				multiplier += data.getValue();
		}
		if (multiplier == 0d)
			return;

		double chance = 1d / multiplier;
		if (mob.getRandom().nextFloat() > chance)
			mob.setAge(growingAge - 1);
	}

	public void slowdownBreeding(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof AgeableMob mob))
			return;

		int growingAge = mob.getAge();
		if (growingAge <= 0)
			return;

		double multiplier = 0d;
		for (LivestockData data : breedingCooldown){
			if (data.matches(mob))
				multiplier += data.getValue();
		}
		if (multiplier == 0d)
			return;

		double chance = 1d / multiplier;
		if (mob.getRandom().nextFloat() > chance)
			mob.setAge(growingAge + 1);
	}

	public void slowdownEggLay(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof Chicken chicken))
			return;

		int timeUntilNextEgg = chicken.eggTime;
		if (timeUntilNextEgg < 0)
			return;

		double multiplier = 0d;
		for (LivestockData data : eggLaySlowdown){
			if (data.matches(chicken))
				multiplier += data.getValue();
		}
		if (multiplier == 0d)
			return;

		double chance = 1d / multiplier;
		if (chicken.getRandom().nextFloat() > chance)
			chicken.eggTime += 1;
	}

	public void cowMilkTick(LivingEvent.LivingTickEvent event) {
		if (event.getEntity().tickCount % 20 != 0 ||
				!(event.getEntity() instanceof Animal animal))
			return;

		CompoundTag cowNBT = animal.getPersistentData();
		int milkCooldown = cowNBT.getInt(MILK_COOLDOWN);
		if (milkCooldown > 0)
			milkCooldown -= 20;
		cowNBT.putInt(MILK_COOLDOWN, milkCooldown);
	}

	@SubscribeEvent
	public void onAnimalMilk(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| !(event.getTarget() instanceof Animal animal)
				|| animal.getAge() < 0)
			return;

		int cooldown = 0;
		for (LivestockData data : cowMilkCooldown){
			if (data.matches(animal)) {
				cooldown = (int) data.getValue();
				break;
			}
		}
		if (cooldown == 0)
			return;

		Player player = event.getEntity();
		InteractionHand hand = event.getHand();
		ItemStack equipped = player.getItemInHand(hand);
		if (equipped.isEmpty() || equipped.getItem() == Items.AIR)
			return;
		Item item = equipped.getItem();
		if ((!FluidUtil.getFluidHandler(equipped).isPresent() || !FluidStack.loadFluidStackFromNBT(equipped.getTag()).isEmpty()) && (!(animal instanceof MushroomCow) || item != Items.BOWL))
			return;
		CompoundTag cowNBT = animal.getPersistentData();
		int milkCooldown = cowNBT.getInt(MILK_COOLDOWN);
		if (milkCooldown > 0) {
			event.setCanceled(true);
			if (!player.level.isClientSide) {
				animal.playSound(SoundEvents.COW_HURT, 0.4F, (event.getEntity().level.random.nextFloat() - event.getEntity().level.random.nextFloat()) * 0.2F + 1.2F);
				MutableComponent message = Component.translatable(MILK_COOLDOWN_TRANSLATION, animal.getDisplayName());
				player.displayClientMessage(message, true);
			}
			else
				event.setCancellationResult(InteractionResult.SUCCESS);
		}
		else {
			milkCooldown = cooldown * 20;
			cowNBT.putInt(MILK_COOLDOWN, milkCooldown);
			player.swing(event.getHand());
		}
	}

	@SubscribeEvent
	public void failBreeding(BabyEntitySpawnEvent event) {
		if (!this.isEnabled())
			return;

		double failChance = 0d;
		int c = 0;
		for (LivestockData data : breedingFailChance){
			if (data.matches(event.getParentA())) {
				failChance += data.getValue();
				c++;
			}
		}
		if (c == 0d)
			return;

		if (event.getParentA().getRandom().nextFloat() < failChance / c)
			event.setCanceled(true);
	}

	private static final String path = "livestock/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "increase_feathers", new DropMultiplierModifier.Builder(Items.FEATHER, 2f)
				.build());
		provider.add(path + "lower_chicken", new DropMultiplierModifier.Builder(Items.CHICKEN, 0.2f)
				.build());
		provider.add(path + "lower_cooked_chicken", new DropMultiplierModifier.Builder(Items.COOKED_CHICKEN, 0.2f)
				.build());
		provider.add(path + "lower_rabbit", new DropMultiplierModifier.Builder(Items.RABBIT, 0.75f)
				.build());
		provider.add(path + "lower_cooked_rabbit", new DropMultiplierModifier.Builder(Items.COOKED_RABBIT, 0.75f)
				.build());
		provider.add(path + "lower_mutton", new DropMultiplierModifier.Builder(Items.MUTTON, 0.2f)
				.build());
		provider.add(path + "lower_cooked_mutton", new DropMultiplierModifier.Builder(Items.COOKED_MUTTON, 0.2f)
				.build());
		provider.add(path + "lower_beef", new DropMultiplierModifier.Builder(Items.BEEF, 0.2f)
				.build());
		provider.add(path + "lower_cooked_beef", new DropMultiplierModifier.Builder(Items.COOKED_BEEF, 0.2f)
				.build());
		provider.add(path + "lower_porkchop", new DropMultiplierModifier.Builder(Items.PORKCHOP, 0.4f)
				.build());
		provider.add(path + "lower_cooked_porkchop", new DropMultiplierModifier.Builder(Items.COOKED_PORKCHOP, 0.4f)
				.build());
		provider.add(path + "lower_cod", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.COD)).build()).build()
		},
				Items.COD,
				0.25f)
				.build());
		provider.add(path + "lower_cooked_cod", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.COD)).build()).build()
		},
				Items.COOKED_COD,
				0.25f)
				.build());
		provider.add(path + "lower_salmon", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.SALMON)).build()).build()
		},
				Items.SALMON,
				0.25f)
				.build());
		provider.add(path + "lower_cooked_salmon", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.SALMON)).build()).build()
		},
				Items.COOKED_SALMON,
				0.25f)
				.build());
		provider.add(path + "lower_pufferfish", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.PUFFERFISH)).build()).build()
		},
				Items.PUFFERFISH,
				0.3f)
				.build());
		provider.add(path + "lower_tropical_fish", new DropMultiplierModifier.Builder(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.TROPICAL_FISH)).build()).build()
		},
				Items.TROPICAL_FISH,
				0.3f)
				.build());
	}
}