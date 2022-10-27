package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

@Label(name = "Livestock Slowdown", description = "Slower breeding, Growing, Egging and Milking")
public class LivestockSlowdown extends Feature {

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

	public LivestockSlowdown(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		childGrowthMultiplierConfig = ITCommonConfig.builder
				.comment("Increases the time required for Baby Animals to grow (e.g. at 2.0 Animals will take twice to grow).\n1.0 will make Animals grow like normal.")
				.defineInRange("Childs Growth Multiplier", childGrowthMultiplier, 1.0d, 128d);
		childGrowthVillagersConfig = ITCommonConfig.builder
				.comment("If true, 'Childs Growth Multiplier' will be applied to villagers too.")
				.define("Childs Growth Villagers", childGrowthVillagers);
		breedingMultiplierConfig = ITCommonConfig.builder
				.comment("Increases the time required for Animals to breed again (e.g. at 2.0 Animals will take twice to be able to breed again).\n1.0 will make Animals breed like normal.")
				.defineInRange("Breeding Time Multiplier", breedingMultiplier, 1.0d, 128d);
		eggLayMultiplierConfig = ITCommonConfig.builder
				.comment("Increases the time required for Chickens to lay an egg (e.g. at 2.0 Chickens will take twice the time to lay an egg).\n1.0 will make chickens lay eggs like normal.")
				.defineInRange("Egg Lay Multiplier", 3.0d, 1.0d, 128d);
		cowMilkDelayConfig = ITCommonConfig.builder
				.comment("Seconds before a cow can be milked again. This applies to Mooshroom stew too.\n0 will disable this feature.")
				.defineInRange("Cow Milk Delay", cowMilkDelay, 0, Integer.MAX_VALUE);
		ITCommonConfig.builder.pop();
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
	public void slowdownAnimalGrowth(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;
		if (this.childGrowthMultiplier == 1d)
			return;
		if (!(event.getEntity() instanceof Animal) && !(event.getEntity() instanceof AbstractVillager))
			return;
		if (event.getEntity() instanceof AbstractVillager && !this.childGrowthVillagers)
			return;
		AgeableMob entity = (AgeableMob) event.getEntity();
		int growingAge = entity.getAge();
		if (growingAge >= 0)
			return;
		double chance = 1d / this.childGrowthMultiplier;
		if (entity.getRandom().nextFloat() > chance)
			entity.setAge(growingAge - 1);
	}

	@SubscribeEvent
	public void slowdownBreeding(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;
		if (this.breedingMultiplier == 1d)
			return;
		if (!(event.getEntity() instanceof Animal))
			return;
		AgeableMob entity = (AgeableMob) event.getEntity();
		int growingAge = entity.getAge();
		if (growingAge <= 0)
			return;
		double chance = 1d / this.breedingMultiplier;
		if (entity.getRandom().nextFloat() > chance)
			entity.setAge(growingAge + 1);
	}

	@SubscribeEvent
	public void slowdownEggLay(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;
		if (this.eggLayMultiplier == 1d)
			return;
		if (!(event.getEntity() instanceof Chicken chicken))
			return;
		int timeUntilNextEgg = chicken.eggTime;
		if (timeUntilNextEgg < 0)
			return;
		double chance = 1d / this.eggLayMultiplier;
		if (chicken.getRandom().nextFloat() > chance)
			chicken.eggTime += 1;
	}

	@SubscribeEvent
	public void cowMilkTick(LivingEvent.LivingTickEvent event) {
		if (!this.isEnabled())
			return;
		if (this.cowMilkDelay == 0)
			return;
		if (event.getEntity().tickCount % 20 != 0)
			return;
		if (!(event.getEntity() instanceof Cow cow))
			return;
		CompoundTag cowNBT = cow.getPersistentData();
		int milkCooldown = cowNBT.getInt(Strings.Tags.MILK_COOLDOWN);
		if (milkCooldown > 0)
			milkCooldown -= 20;
		cowNBT.putInt(Strings.Tags.MILK_COOLDOWN, milkCooldown);
	}

	@SubscribeEvent
	public void onCowMilk(PlayerInteractEvent.EntityInteract event) {
		if (!this.isEnabled())
			return;
		if (this.cowMilkDelay == 0)
			return;
		if (!(event.getTarget() instanceof Cow cow))
			return;
		if (cow.getAge() < 0)
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
			milkCooldown = this.cowMilkDelay * 20;
			cowNBT.putInt(Strings.Tags.MILK_COOLDOWN, milkCooldown);
			player.swing(event.getHand());
		}
	}
}