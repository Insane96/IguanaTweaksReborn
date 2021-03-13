package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Random;

@Label(name = "Livestock Slowdown", description = "Slower breeding, Growing, Egging and Milking")
public class LivestockSlowdownFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> childGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> childGrowthVillagersConfig;
	private final ForgeConfigSpec.ConfigValue<Double> breedingMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> eggLayMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> cowMilkDelayConfig;

	public double childGrowthMultiplier = 3.0d;
	public boolean childGrowthVillagers = true;
	public double breedingMultiplier = 3.5d;
	public double eggLayMultiplier = 3.0d;
	public int cowMilkDelay = 1200;

	public LivestockSlowdownFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		childGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for Baby Animals to grow (e.g. at 2.0 Animals will take twice to grow).\n1.0 will make Animals grow like normal.")
				.defineInRange("Childs Growth Multiplier", childGrowthMultiplier, 1.0d, 128d);
		childGrowthVillagersConfig = Config.builder
				.comment("If true, 'Childs Growth Multiplier' will be applied to villagers too.")
				.define("Childs Growth Villagers", childGrowthVillagers);
		breedingMultiplierConfig = Config.builder
				.comment("Increases the time required for Animals to breed again (e.g. at 2.0 Animals will take twice to be able to breed again).\n1.0 will make Animals breed like normal.")
				.defineInRange("Breeding Time Multiplier", breedingMultiplier, 1.0d, 128d);
		eggLayMultiplierConfig = Config.builder
				.comment("Increases the time required for Chickens to lay an egg (e.g. at 2.0 Chickens will take twice the time to lay an egg).\n1.0 will make chickens lay eggs like normal.")
				.defineInRange("Egg Lay Multiplier", 3.0d, 1.0d, 128d);
		cowMilkDelayConfig = Config.builder
				.comment("Seconds before a cow can be milked again. This applies to Mooshroom stew too.\n0 will disable this feature.")
				.defineInRange("Cow Milk Delay", cowMilkDelay, 0, Integer.MAX_VALUE);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.childGrowthMultiplier = this.childGrowthMultiplierConfig.get();
		this.childGrowthVillagers = this.childGrowthVillagersConfig.get();
		this.breedingMultiplier = this.breedingMultiplierConfig.get();
		this.eggLayMultiplier = this.eggLayMultiplierConfig.get();
		this.cowMilkDelay = this.cowMilkDelayConfig.get();
	}

	@SubscribeEvent
	public void slowdownAnimalGrowth(LivingEvent.LivingUpdateEvent event) {
		if (!this.isEnabled())
			return;
		if (this.childGrowthMultiplier == 1d)
			return;
		if (!(event.getEntityLiving() instanceof AnimalEntity) && !(event.getEntityLiving() instanceof AbstractVillagerEntity))
			return;
		if (event.getEntityLiving() instanceof AbstractVillagerEntity && !this.childGrowthVillagers)
			return;
		AgeableEntity entity = (AgeableEntity) event.getEntityLiving();
		Random rand = event.getEntityLiving().world.rand;
		int growingAge = entity.getGrowingAge();
		if (growingAge >= 0)
			return;
		double chance = 1d / this.childGrowthMultiplier;
		if (rand.nextFloat() > chance)
			entity.setGrowingAge(growingAge - 1);
	}

	@SubscribeEvent
	public void slowdownBreeding(LivingEvent.LivingUpdateEvent event) {
		if (!this.isEnabled())
			return;
		if (this.breedingMultiplier == 1d)
			return;
		if (!(event.getEntityLiving() instanceof AnimalEntity))
			return;
		AgeableEntity entity = (AgeableEntity) event.getEntityLiving();
		Random rand = event.getEntityLiving().world.rand;
		int growingAge = entity.getGrowingAge();
		if (growingAge <= 0)
			return;
		double chance = 1d / this.breedingMultiplier;
		if (rand.nextFloat() > chance)
			entity.setGrowingAge(growingAge + 1);
	}

	@SubscribeEvent
	public void slowdownEggLay(LivingEvent.LivingUpdateEvent event) {
		if (!this.isEnabled())
			return;
		if (this.eggLayMultiplier == 1d)
			return;
		if (!(event.getEntityLiving() instanceof ChickenEntity))
			return;
		ChickenEntity chicken = (ChickenEntity) event.getEntityLiving();
		Random rand = event.getEntityLiving().world.rand;
		int timeUntilNextEgg = chicken.timeUntilNextEgg;
		if (timeUntilNextEgg < 0)
			return;
		double chance = 1d / this.eggLayMultiplier;
		if (rand.nextFloat() > chance)
			chicken.timeUntilNextEgg += 1;
	}

	@SubscribeEvent
	public void cowMilkTick(LivingEvent.LivingUpdateEvent event) {
		if (!this.isEnabled())
			return;
		if (this.cowMilkDelay == 0)
			return;
		if (event.getEntityLiving().ticksExisted % 20 != 0)
			return;
		if (!(event.getEntityLiving() instanceof CowEntity))
			return;
		CowEntity cow = (CowEntity) event.getEntityLiving();
		CompoundNBT cowNBT = cow.getPersistentData();
		int milkCooldown = cowNBT.getInt(Strings.NBTTags.MILK_COOLDOWN);
		if (milkCooldown > 0)
			milkCooldown -= 20;
		cowNBT.putInt(Strings.NBTTags.MILK_COOLDOWN, milkCooldown);
	}

	@SubscribeEvent
	public void onCowMilk(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled())
			return;
		if (this.cowMilkDelay == 0)
			return;
		if (!(event.getTarget() instanceof CowEntity))
			return;
		CowEntity cow = (CowEntity) event.getTarget();
		if (cow.getGrowingAge() < 0)
			return;
		PlayerEntity player = event.getPlayer();
		Hand hand = event.getHand();
		ItemStack equipped = player.getHeldItem(hand);
		if (equipped.isEmpty() || equipped.getItem() == Items.AIR)
			return;
		Item item = equipped.getItem();
		if ((!FluidUtil.getFluidHandler(equipped).isPresent() || !FluidStack.loadFluidStackFromNBT(equipped.getTag()).isEmpty()) && (!(cow instanceof MooshroomEntity) || item != Items.BOWL))
			return;
		CompoundNBT cowNBT = cow.getPersistentData();
		int milkCooldown = cowNBT.getInt(Strings.NBTTags.MILK_COOLDOWN);
		if (milkCooldown > 0) {
			event.setCanceled(true);
			if (!player.world.isRemote) {
				cow.playSound(SoundEvents.ENTITY_COW_HURT, 0.4F, (event.getEntity().world.rand.nextFloat() - event.getEntity().world.rand.nextFloat()) * 0.2F + 1.2F);
				String animal = cow instanceof MooshroomEntity ? Strings.Translatable.MOOSHROOM_COOLDOWN : Strings.Translatable.COW_COOLDOWN;
				String yetReady = Strings.Translatable.YET_READY;
				ITextComponent message = new TranslationTextComponent(animal).appendString(" ").append(new TranslationTextComponent(yetReady));
				player.sendStatusMessage(message, true);
			}
		}
		else {
			milkCooldown = this.cowMilkDelay * 20;
			cowNBT.putInt(Strings.NBTTags.MILK_COOLDOWN, milkCooldown);
			event.setResult(Event.Result.ALLOW);
			player.swingArm(event.getHand());
		}
	}
}
