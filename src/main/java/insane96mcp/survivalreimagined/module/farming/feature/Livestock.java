package insane96mcp.survivalreimagined.module.farming.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.data.lootmodifier.DropMultiplierModifier;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.Strings;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.npc.AbstractVillager;
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

@Label(name = "Livestock", description = "Slower breeding, Growing, Egging and Milking. Lower yield.")
@LoadFeature(module = Modules.Ids.FARMING)
public class Livestock extends Feature {

	//TODO Change the eating grass goal to make sheep only eat tall grass and no longer grass blocks
	private static final ResourceLocation NO_LIVESTOCK_SLOWDOWN = new ResourceLocation(SurvivalReimagined.MOD_ID, "no_livestock_slowdown");

	@Config(min = 1d, max = 128d)
	@Label(name = "Childs Growth Multiplier", description = "Increases the time required for Baby Animals to grow (e.g. at 2.0 Animals will take twice to grow).\n1.0 will make Animals grow like normal.")
	public static Double childGrowthMultiplier = 3.0d;
	@Config
	@Label(name = "Childs Growth Villagers", description = "If true, 'Childs Growth Multiplier' will be applied to villagers too.")
	public static Boolean childGrowthVillagers = true;
	@Config(min = 1d, max = 128d)
	@Label(name = "Breeding Time Multiplier", description = "Increases the time required for Animals to breed again (e.g. at 2.0 Animals will take twice to be able to breed again).\n1.0 will make Animals breed like normal.")
	public static Double breedingMultiplier = 2d;
	@Config(min = 0d, max = 1d)
	@Label(name = "Breeding Fail Chance", description = "Chance for a baby to not to be born.")
	public static Double breedingFailChance = 0.75d;
	@Config(min = 1d, max = 128d)
	@Label(name = "Egg Lay Multiplier", description = "Increases the time required for Chickens to lay an egg (e.g. at 2.0 Chickens will take twice the time to lay an egg).\n1.0 will make chickens lay eggs like normal.")
	public static Double eggLayMultiplier = 3.0d;
	@Config(min = 0)
	@Label(name = "Cow Milk Delay", description = "Seconds before a cow can be milked again. This applies to Mooshroom stew too.\n0 will disable this feature.")
	public static Integer cowMilkDelay = 1200;

	public Livestock(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void slowdownAnimalGrowth(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| childGrowthMultiplier == 1d
				|| isEntityBlacklisted(event.getEntity()))
			return;
		if (!(event.getEntity() instanceof Animal) && !(event.getEntity() instanceof AbstractVillager))
			return;
		if (event.getEntity() instanceof AbstractVillager && !childGrowthVillagers)
			return;
		AgeableMob entity = (AgeableMob) event.getEntity();
		int growingAge = entity.getAge();
		if (growingAge >= 0)
			return;
		double chance = 1d / childGrowthMultiplier;
		if (entity.getRandom().nextFloat() > chance)
			entity.setAge(growingAge - 1);
	}

	@SubscribeEvent
	public void failBreeding(BabyEntitySpawnEvent event) {
		if (!this.isEnabled()
				|| breedingFailChance == 0d
				|| !(event.getParentA() instanceof Animal animal)
				|| isEntityBlacklisted(event.getParentA()))
			return;
		if (animal.getRandom().nextFloat() < breedingFailChance)
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void slowdownBreeding(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| breedingMultiplier == 1d
				|| !(event.getEntity() instanceof Animal)
				|| isEntityBlacklisted(event.getEntity()))
			return;
		AgeableMob entity = (AgeableMob) event.getEntity();
		int growingAge = entity.getAge();
		if (growingAge <= 0)
			return;
		double chance = 1d / breedingMultiplier;
		if (entity.getRandom().nextFloat() > chance)
			entity.setAge(growingAge + 1);
	}

	@SubscribeEvent
	public void slowdownEggLay(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| eggLayMultiplier == 1d
				|| !(event.getEntity() instanceof Chicken chicken)
				|| isEntityBlacklisted(chicken))
			return;
		int timeUntilNextEgg = chicken.eggTime;
		if (timeUntilNextEgg < 0)
			return;
		double chance = 1d / eggLayMultiplier;
		if (chicken.getRandom().nextFloat() > chance)
			chicken.eggTime += 1;
	}

	@SubscribeEvent
	public void cowMilkTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled()
				|| cowMilkDelay == 0
				|| event.getEntity().tickCount % 20 != 0
				|| !(event.getEntity() instanceof Cow cow)
				|| isEntityBlacklisted(cow))
			return;
		CompoundTag cowNBT = cow.getPersistentData();
		int milkCooldown = cowNBT.getInt(Strings.Tags.MILK_COOLDOWN);
		if (milkCooldown > 0)
			milkCooldown -= 20;
		cowNBT.putInt(Strings.Tags.MILK_COOLDOWN, milkCooldown);
	}

	@SubscribeEvent
	public void onCowMilk(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| cowMilkDelay == 0
				|| !(event.getTarget() instanceof Cow cow)
				|| isEntityBlacklisted(cow)
				|| cow.getAge() < 0)
			return;
		Player player = event.getEntity();
		InteractionHand hand = event.getHand();
		ItemStack equipped = player.getItemInHand(hand);
		if (equipped.isEmpty() || equipped.getItem() == Items.AIR)
			return;
		Item item = equipped.getItem();
		if ((!FluidUtil.getFluidHandler(equipped).isPresent() || !FluidStack.loadFluidStackFromNBT(equipped.getTag()).isEmpty()) && (!(cow instanceof MushroomCow) || item != Items.BOWL))
			return;
		CompoundTag cowNBT = cow.getPersistentData();
		int milkCooldown = cowNBT.getInt(Strings.Tags.MILK_COOLDOWN);
		if (milkCooldown > 0) {
			event.setCanceled(true);
			if (!player.level.isClientSide) {
				cow.playSound(SoundEvents.COW_HURT, 0.4F, (event.getEntity().level.random.nextFloat() - event.getEntity().level.random.nextFloat()) * 0.2F + 1.2F);
				String animal = cow instanceof MushroomCow ? Strings.Translatable.MOOSHROOM_COOLDOWN : Strings.Translatable.COW_COOLDOWN;
				String yetReady = Strings.Translatable.YET_READY;
				MutableComponent message = Component.translatable(animal).append(" ").append(Component.translatable(yetReady));
				player.displayClientMessage(message, true);
			}
			else
				event.setCancellationResult(InteractionResult.SUCCESS);
		}
		else {
			milkCooldown = cowMilkDelay * 20;
			cowNBT.putInt(Strings.Tags.MILK_COOLDOWN, milkCooldown);
			player.swing(event.getHand());
		}
	}

	public static boolean isEntityBlacklisted(Entity entity) {
		return Utils.isEntityInTag(entity, NO_LIVESTOCK_SLOWDOWN);
	}

	private static final String path = "livestock/";

	public static void addGlobalLoot(GlobalLootModifierProvider provider) {
		provider.add(path + "lower_chicken", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.CHICKEN,
				0.2f
		));
		provider.add(path + "lower_cooked_chicken", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.COOKED_CHICKEN,
				0.2f
		));
		provider.add(path + "lower_mutton", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.MUTTON,
				0.2f
		));
		provider.add(path + "lower_cooked_mutton", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.COOKED_MUTTON,
				0.25f
		));
		provider.add(path + "lower_beef", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.BEEF,
				0.25f
		));
		provider.add(path + "lower_cooked_beef", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.COOKED_BEEF,
				0.25f
		));
		provider.add(path + "lower_cow_leather", new DropMultiplierModifier(
				new LootItemCondition[] {
						LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, new EntityPredicate.Builder().entityType(EntityTypePredicate.of(EntityType.COW)).build()).build()
				},
				Items.LEATHER,
				0.5f
		));
		provider.add(path + "lower_porkchop", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.PORKCHOP,
				0.4f
		));
		provider.add(path + "lower_cooked_porkchop", new DropMultiplierModifier(
				new LootItemCondition[0],
				Items.COOKED_PORKCHOP,
				0.4f
		));
		//TODO Fish
	}
}