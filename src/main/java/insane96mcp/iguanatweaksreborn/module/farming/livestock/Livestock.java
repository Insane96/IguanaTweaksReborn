package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import com.google.gson.annotations.SerializedName;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modifier.Modifier;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.network.message.ForgeDataIntSync;
import insane96mcp.iguanatweaksreborn.setup.IntegratedPack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Label(name = "Livestock", description = "Slower breeding, Growing, Egging and Milking. Lower yield.")
@LoadFeature(module = Modules.Ids.FARMING)
public class Livestock extends Feature {

	public static final String MILK_COOLDOWN_LANG = IguanaTweaksReborn.MOD_ID + ".milk_cooldown";
	public static final String MILK_COOLDOWN = IguanaTweaksReborn.RESOURCE_PREFIX + "milk_cooldown";

	public static final String FED_TIME = IguanaTweaksReborn.RESOURCE_PREFIX + "fed_time";

	@Config
	@Label(name = "Chicken from egg chance", description = "Changes the chance for a chicken to come out from an egg (1 in this value). Vanilla is 8")
	public static Integer chickenFromEggChance = 8;

	@Config
	@Label(name = "Faster egg time", description = "If not 0, chickens will no longer breed, instead when fed will make eggs twice as fast for the seconds set here.")
	public static Integer fasterEggTime = 900;

	@Config
	@Label(name = "Data Pack", description = "Enables a data pack that changes food drops and slows down growing, breeding, egging etc")
	public static Boolean dataPack = true;

	@Config
	@Label(name = "Milk Cooldown", description = "Seconds until you can milk cows (or stew mooshrooms)")
	public static Integer milkingCooldown = 1200;

	public Livestock(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedPack.addPack(new IntegratedPack(PackType.SERVER_DATA, "livestock_changes", Component.literal("IguanaTweaks Reborn Livestock Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && dataPack));
	}

	@SubscribeEvent
	public void onSheepJoinLevel(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Sheep sheep))
			return;

		sheep.goalSelector.removeGoal(sheep.eatBlockGoal);
		sheep.eatBlockGoal = new SREatBlock(sheep);
		sheep.goalSelector.addGoal(5, sheep.eatBlockGoal);
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;

		if (!event.getEntity().level().isClientSide) {
			slowdownAnimalGrowth(event);
			slowdownBreeding(event);
			liveDeath(event);
			eggLay(event);
			tickFedTime(event);
		}
		cowMilkTick(event);
	}

	@SubscribeEvent
	public void onTryToSeedChickens(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled()
				|| !(event.getTarget() instanceof Chicken chicken)
		        || !event.getItemStack().is(Crops.CHICKEN_FOOD_ITEMS))
			return;

		event.setCanceled(true);
		if (chicken.getPersistentData().getInt(FED_TIME) <= 0) {
			chicken.getPersistentData().putInt(FED_TIME, fasterEggTime * 20);
			event.getEntity().swing(event.getHand());
			chicken.level().addParticle(ParticleTypes.HAPPY_VILLAGER, chicken.getX(), chicken.getY() + chicken.getBbHeight() + 0.5D, chicken.getZ(), 0.0D, 0.0D, 0.0D);
			if (!event.getEntity().getAbilities().instabuild)
				event.getEntity().getItemInHand(event.getHand()).shrink(1);
		}
	}

	public static boolean canSheepRegrowWool(Mob mob) {
		List<Modifier> modifiersToApply = new ArrayList<>();
		LivestockDataReloadListener.LIVESTOCK_DATA.stream()
				.filter(data -> data.matches(mob))
				.forEach(data -> modifiersToApply.addAll(data.sheepWoolGrowthChanceModifiers));
		float chance = Modifier.applyModifiers(1f, modifiersToApply, mob.level(), mob.blockPosition(), mob);
		if (chance >= 1d)
			return true;
		return mob.getRandom().nextFloat() < chance;
	}

	public static void liveDeath(LivingEvent.LivingTickEvent event) {
        //noinspection DataFlowIssue
        if (!(event.getEntity() instanceof AgeableMob ageableMob)
				|| (ageableMob.level().getServer().getTickCount() + ageableMob.getId()) % 20 != 0
				|| ageableMob.isBaby())
			return;

		boolean forceUpdateScale = false;
		int age = ageableMob.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "age");
		int maxAge = ageableMob.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "max_age");
		if (maxAge == 0) {
			for (LivestockData data : LivestockDataReloadListener.LIVESTOCK_DATA) {
                if (data.matches(ageableMob) && data.livingDays != null) {
					maxAge = (int) (data.getLivingDays(ageableMob) * 20 * 60);
					ageableMob.getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "max_age", maxAge);
                    break;
                }
			}
			if (maxAge == 0)
				return;
			forceUpdateScale = true;
		}
		age++;
		if (age >= maxAge) {
			ageableMob.kill();
			return;
		}
		if (age >= maxAge * 0.75)
			MCUtils.applyModifier(ageableMob, Attributes.MOVEMENT_SPEED, UUID.fromString("e2083ae7-e37a-47c4-ab3e-84cf14fe6b6c"), "Old animal modifier", -0.3d, AttributeModifier.Operation.MULTIPLY_BASE, false);
		if (ModList.get().isLoaded("pehkui") && ((ageableMob.level().getServer().getTickCount() + ageableMob.getId()) % 100 == 0 || forceUpdateScale))
			PehkuiIntegration.setSize(ageableMob, (float) age / (float) maxAge);
		ageableMob.getPersistentData().putInt(IguanaTweaksReborn.RESOURCE_PREFIX + "age", age);
    }

	public static float getAgeRatio(AgeableMob mob) {
		return (float) mob.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "age") / (float) mob.getPersistentData().getInt(IguanaTweaksReborn.RESOURCE_PREFIX + "max_age");
	}

	public static Age getAge(AgeableMob mob) {
		float ratio = getAgeRatio(mob);
		if (ratio < 0.25f)
			return Age.YOUNG;
		if (ratio < 0.5f)
			return Age.ADULT;
		if (ratio < 0.75f)
			return Age.MID_AGE;
		return Age.OLD;
	}

	public void slowdownAnimalGrowth(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof AgeableMob mob))
			return;

		int growingAge = mob.getAge();
		if (growingAge >= 0)
			return;

		List<Modifier> modifiersToApply = new ArrayList<>();
		LivestockDataReloadListener.LIVESTOCK_DATA.stream()
				.filter(data -> data.matches(mob))
				.forEach(data -> modifiersToApply.addAll(data.growthSpeedModifiers));
		float chanceToSlowDown = Modifier.applyModifiers(0f, modifiersToApply, mob.level(), mob.blockPosition(), mob);
		if (chanceToSlowDown <= 1d)
			return;

		double chance = 1d / chanceToSlowDown;
		if (mob.getRandom().nextFloat() > chance)
			mob.setAge(growingAge - 1);
	}

	public void slowdownBreeding(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof AgeableMob mob))
			return;

		int growingAge = mob.getAge();
		if (growingAge <= 0)
			return;

		List<Modifier> modifiersToApply = new ArrayList<>();
		LivestockDataReloadListener.LIVESTOCK_DATA.stream()
				.filter(data -> data.matches(mob))
				.forEach(data -> modifiersToApply.addAll(data.breedingCooldownModifiers));
		float chanceToSlowDown = Modifier.applyModifiers(0f, modifiersToApply, mob.level(), mob.blockPosition(), mob);
		if (chanceToSlowDown <= 1d)
			return;

		double chance = 1d / chanceToSlowDown;
		if (mob.getRandom().nextFloat() > chance)
			mob.setAge(growingAge + 1);
	}

	public void eggLay(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof Chicken chicken)
				|| chicken.isBaby())
			return;

		int timeUntilNextEgg = chicken.eggTime;
		if (timeUntilNextEgg < 0)
			return;

		List<Modifier> modifiersToApply = new ArrayList<>();
		LivestockDataReloadListener.LIVESTOCK_DATA.stream()
				.filter(data -> data.matches(chicken))
				.forEach(data -> modifiersToApply.addAll(data.eggLayCooldownModifiers));
		float chanceToSlowDown = Modifier.applyModifiers(0f, modifiersToApply, chicken.level(), chicken.blockPosition(), chicken);
		if (chanceToSlowDown <= 1d)
			return;

		int fedTime = chicken.getPersistentData().getInt(FED_TIME);

		double chance = 1d / chanceToSlowDown;
		if (chicken.getRandom().nextFloat() > chance) {
			timeUntilNextEgg += 1;
			if (fedTime > 0)
				timeUntilNextEgg += 1;
		}
		if (fedTime > 0)
			timeUntilNextEgg -= 1;

		chicken.eggTime = timeUntilNextEgg;
	}

	public void tickFedTime(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof Chicken chicken)
				|| chicken.isBaby()
				|| chicken.tickCount % 20 != 0)
			return;
		int fedTime = chicken.getPersistentData().getInt(FED_TIME);
		if (fedTime <= 0)
			return;

		chicken.getPersistentData().putInt(FED_TIME, fedTime - 20);
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
			if (!player.level().isClientSide) {
				animal.playSound(SoundEvents.COW_HURT, 0.4F, (event.getEntity().level().random.nextFloat() - event.getEntity().level().random.nextFloat()) * 0.2F + 1.2F);
				MutableComponent message = Component.translatable(MILK_COOLDOWN_LANG, animal.getDisplayName());
				player.displayClientMessage(message, true);
			}
			else
				event.setCancellationResult(InteractionResult.SUCCESS);
		}
		else if (!animal.level().isClientSide) {
			List<Modifier> modifiersToApply = new ArrayList<>();
			LivestockDataReloadListener.LIVESTOCK_DATA.stream()
					.filter(data -> data.matches(animal))
					.forEach(data -> modifiersToApply.addAll(data.cowFluidCooldownModifiers));
			float cooldown = Modifier.applyModifiers(milkingCooldown, modifiersToApply, animal.level(), animal.blockPosition(), animal);
			if (cooldown <= 0)
				return;

			milkCooldown = (int) (cooldown * 20);
			cowNBT.putInt(MILK_COOLDOWN, milkCooldown);
			ForgeDataIntSync.sync(animal, MILK_COOLDOWN, milkCooldown);
			//player.swing(event.getHand());
		}
	}

	@SubscribeEvent
	public void onEntityLoad(EntityJoinLevelEvent event) {
		if (!this.isEnabled()
				|| event.getLevel().isClientSide
				|| !(event.getEntity() instanceof LivingEntity livingEntity)
				|| !livingEntity.getPersistentData().contains(MILK_COOLDOWN))
			return;

		ForgeDataIntSync.sync(livingEntity, MILK_COOLDOWN, livingEntity.getPersistentData().getInt(MILK_COOLDOWN));
	}

	@SubscribeEvent
	public void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
		if (!this.isEnabled()
				|| event.getEntity().level().isClientSide)
			return;

		for (Entity entity : ((ServerLevel) event.getEntity().level()).getEntities().getAll()) {
			if (entity instanceof LivingEntity livingEntity && livingEntity.getPersistentData().contains(MILK_COOLDOWN))
				ForgeDataIntSync.sync((ServerPlayer) event.getEntity(), livingEntity, MILK_COOLDOWN, livingEntity.getPersistentData().getInt(MILK_COOLDOWN));
		}
	}

	@SubscribeEvent
	public void failBreeding(BabyEntitySpawnEvent event) {
		if (!this.isEnabled())
			return;

		Mob mob = event.getParentA().getRandom().nextBoolean() ? event.getParentA() : event.getParentB();
		List<Modifier> modifiersToApply = new ArrayList<>();
		LivestockDataReloadListener.LIVESTOCK_DATA.stream()
				.filter(data -> data.matches(mob))
				.forEach(data -> modifiersToApply.addAll(data.breedingCooldownModifiers));
		float failChance = Modifier.applyModifiers(0f, modifiersToApply, mob.level(), mob.blockPosition(), mob);
		if (failChance <= 0)
			return;

		if (mob.getRandom().nextFloat() < failChance)
			event.setCanceled(true);
	}

	public enum Age {
		@SerializedName("young")
		YOUNG,
		@SerializedName("adult")
		ADULT,
		@SerializedName("mid_age")
		MID_AGE,
		@SerializedName("old")
		OLD
	}
}