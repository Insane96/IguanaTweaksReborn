package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.RandomHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.block.*;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Collections;
import java.util.Random;

public class FarmingModule {

	public static class Livestock {
		public static void slowdownAnimalGrowth(LivingEvent.LivingUpdateEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Livestock.childGrowthMultiplier == 1d)
				return;
			if (!(event.getEntityLiving() instanceof AnimalEntity))
				return;
			AgeableEntity entity = (AgeableEntity) event.getEntityLiving();
			Random rand = event.getEntityLiving().world.rand;
			int growingAge = entity.getGrowingAge();
			if (growingAge >= 0)
				return;
			double chance = 1d / ModConfig.Farming.Livestock.childGrowthMultiplier;
			if (rand.nextFloat() > chance)
				entity.setGrowingAge(growingAge - 1);
		}

		public static void slowdownBreeding(LivingEvent.LivingUpdateEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Livestock.breedingMultiplier == 1d)
				return;
			if (!(event.getEntityLiving() instanceof AnimalEntity))
				return;
			AgeableEntity entity = (AgeableEntity) event.getEntityLiving();
			Random rand = event.getEntityLiving().world.rand;
			int growingAge = entity.getGrowingAge();
			if (growingAge <= 0)
				return;
			double chance = 1d / ModConfig.Farming.Livestock.breedingMultiplier;
			if (rand.nextFloat() > chance)
				entity.setGrowingAge(growingAge + 1);
		}

		public static void slowdownEggLay(LivingEvent.LivingUpdateEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Livestock.eggLayMultiplier == 1d)
				return;
			if (!(event.getEntityLiving() instanceof ChickenEntity))
				return;
			ChickenEntity chicken = (ChickenEntity) event.getEntityLiving();
			Random rand = event.getEntityLiving().world.rand;
			int timeUntilNextEgg = chicken.timeUntilNextEgg;
			if (timeUntilNextEgg < 0)
				return;
			double chance = 1d / ModConfig.Farming.Livestock.eggLayMultiplier;
			if (rand.nextFloat() > chance)
				chicken.timeUntilNextEgg += 1;
		}

		public static void cowMilkTick(LivingEvent.LivingUpdateEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Livestock.cowMilkDelay == 0)
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

		public static void onCowMilk(PlayerInteractEvent.EntityInteract event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Livestock.cowMilkDelay == 0)
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
					cow.playSound(SoundEvents.ENTITY_COW_HURT, 0.4F, (event.getEntity().world.rand.nextFloat() - event.getEntity().world.rand.nextFloat()) * 0.2F + 1.0F);
					String animal = cow instanceof MooshroomEntity ? Strings.Translatable.MOOSHROOM_COOLDOWN : Strings.Translatable.COW_COOLDOWN;
					String yetReady = Strings.Translatable.YET_READY;
					ITextComponent message = new TranslationTextComponent(animal).appendString(" ").append(new TranslationTextComponent(yetReady));
					player.sendStatusMessage(message, true);
				}
			}
			else {
				milkCooldown = ModConfig.Farming.Livestock.cowMilkDelay;
				cowNBT.putInt(Strings.NBTTags.MILK_COOLDOWN, milkCooldown);
				event.setResult(Event.Result.ALLOW);
				player.swingArm(event.getHand());
			}
		}
	}

	public static class Agriculture {

		/**
		 * Handles part of crops require water too
		 */
		public static void nerfBonemeal(BonemealEvent event) {
			if (event.getWorld().isRemote)
				return;
			if (!ModConfig.Modules.farming)
				return;
			//If farmland is dry and cropsRequireWater is set to anycase then cancel the event
			if (isAffectedByFarmlandState(event.getWorld(), event.getPos()) && !isCropOnWetFarmland(event.getWorld(), event.getPos()) && ModConfig.Farming.Agriculture.cropsRequireWater.equals(CropsRequireWater.ANY_CASE)) {
				event.setCanceled(true);
				return;
			}
			if (ModConfig.Farming.Agriculture.nerfedBonemeal.equals(NerfedBonemeal.DISABLED))
				return;
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if (state.getBlock() instanceof CropsBlock) {
				boolean isBeetroot = state.getBlock() instanceof BeetrootBlock;
				int age = 0;
				int maxAge = Collections.max(CropsBlock.AGE.getAllowedValues());
				if (isBeetroot) {
					age = state.get(BeetrootBlock.BEETROOT_AGE);
					maxAge = Collections.max(BeetrootBlock.BEETROOT_AGE.getAllowedValues());
				}
				else
					age = state.get(CropsBlock.AGE);
				if (age == maxAge)
					return;
				if (ModConfig.Farming.Agriculture.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
					age += RandomHelper.getInt(event.getWorld().getRandom(), 1, 2);
				else if (ModConfig.Farming.Agriculture.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
					age++;
				if (age > maxAge)
					age = maxAge;
				if (isBeetroot) {
					state = state.with(BeetrootBlock.BEETROOT_AGE, age);
				}
				else
					state = state.with(CropsBlock.AGE, age);
			}
			else if (state.getBlock() instanceof StemBlock) {
				int age = state.get(StemBlock.AGE);
				int maxAge = Collections.max(StemBlock.AGE.getAllowedValues());
				if (age == maxAge)
					return;
				if (ModConfig.Farming.Agriculture.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
					age += RandomHelper.getInt(event.getWorld().getRandom(), 1, 2);
				else if (ModConfig.Farming.Agriculture.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
					age++;
				if (age > maxAge)
					age = maxAge;
				state = state.with(StemBlock.AGE, age);
			}
			else
				return;
			event.getWorld().setBlockState(event.getPos(), state, 3);
			event.setResult(Event.Result.ALLOW);
		}

		public static void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Agriculture.cropsRequireWater.equals(CropsRequireWater.NO))
				return;
			if (!isAffectedByFarmlandState(event.getWorld(), event.getPos()))
				return;
			if (!isCropOnWetFarmland(event.getWorld(), event.getPos()))
				event.setResult(Event.Result.DENY);
		}

		private static boolean isAffectedByFarmlandState(IWorld world, BlockPos cropPos) {
			BlockState state = world.getBlockState(cropPos);
			Block block = state.getBlock();
			return block instanceof CropsBlock || block instanceof StemBlock;
		}

		private static boolean isCropOnWetFarmland(IWorld world, BlockPos cropPos) {
			BlockState sustainState = world.getBlockState(cropPos.down());
			if (!(sustainState.getBlock() instanceof FarmlandBlock))
				return false;
			int moisture = sustainState.get(FarmlandBlock.MOISTURE);
			return moisture >= 7;
		}


		/**
		 * Handles Crop Growth Speed Multiplier and NoSunlight Growth multiplier
		 */
		public static void cropsGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Post event) {
			if (!ModConfig.Modules.farming)
				return;
			if (ModConfig.Farming.Agriculture.cropsGrowthMultiplier == 1.0d && ModConfig.Farming.Agriculture.noSunlightGrowthMultiplier == 1.0d)
				return;
			IWorld world = event.getWorld();
			BlockState state = event.getOriginalState();
			if (!(state.getBlock() instanceof CropsBlock))
				return;
			double chance;
			if (ModConfig.Farming.Agriculture.cropsGrowthMultiplier == 0.0d)
				chance = -1d;
			else
				chance = 1d / ModConfig.Farming.Agriculture.cropsGrowthMultiplier;
			int skyLight = world.getLightFor(LightType.SKY, event.getPos());
			if (skyLight < ModConfig.Farming.Agriculture.minSunlight)
				if (ModConfig.Farming.Agriculture.noSunlightGrowthMultiplier == 0.0d)
					chance = -1d;
				else
					chance *= 1d / ModConfig.Farming.Agriculture.noSunlightGrowthMultiplier;
			if (event.getWorld().getRandom().nextDouble() > chance)
				world.setBlockState(event.getPos(), state, 2);
		}

		public static void plantGrowthMultiplier(BlockEvent.CropGrowEvent.Post event, Class<? extends Block> blockClass, double multiplier) {
			if (!ModConfig.Modules.farming)
				return;
			if (multiplier == 1.0d)
				return;
			IWorld world = event.getWorld();
			BlockState state = event.getOriginalState();
			if (!(state.getBlock().getClass().isInstance(blockClass)))
				return;
			double chance;
			if (multiplier == 0.0d)
				chance = -1d;
			else
				chance = 1d / multiplier;
			if (event.getWorld().getRandom().nextDouble() > chance)
				world.setBlockState(event.getPos(), state, 2);
		}

		public enum NerfedBonemeal {
			DISABLED,
			SLIGHT,
			NERFED
		}

		public enum CropsRequireWater {
			NO,
			BONEMEAL_ONLY,
			ANY_CASE
		}

		public static void disabledHoes(UseHoeEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (event.getPlayer().world.isRemote)
				return;
			if (!ModConfig.Farming.Agriculture.disableLowTierHoes)
				return;
			if (!isHoeDisabled(event.getContext().getItem().getItem()))
				return;
			ItemStack hoe = event.getContext().getItem();
			hoe.damageItem(15, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getContext().getHand()));
			event.getPlayer().sendStatusMessage(new StringTextComponent("This hoe is too weak to be used"), true);
			event.setCanceled(true);
		}

		public static void harderTilling(UseHoeEvent event) {
			if (!ModConfig.Modules.farming)
				return;
			if (event.getPlayer().world.isRemote)
				return;
			if (isHoeDisabled(event.getContext().getItem().getItem()))
				return;
			if (event.getContext().getFace() == Direction.DOWN || !event.getContext().getWorld().isAirBlock(event.getContext().getPos().up()))
				return;
			BlockState blockstate = HoeItem.HOE_LOOKUP.get(event.getContext().getWorld().getBlockState(event.getContext().getPos()).getBlock());
			if (blockstate == null || blockstate.getBlock() != Blocks.FARMLAND)
				return;
			ItemStack stack = event.getContext().getItem();
			int cooldown = 0;
			for (ModConfig.Farming.Agriculture.HoeCooldown hoeCooldown : ModConfig.Farming.Agriculture.hoesCooldowns) {
				//System.out.println(hoeTillChance.cooldown);
				if (Utils.isInTagOrItem(hoeCooldown, stack.getItem(), null)) {
					cooldown = hoeCooldown.cooldown;
				}
			}
			if (ModConfig.Farming.Agriculture.hoesDamageOnUseMultiplier > 1)
				stack.damageItem(ModConfig.Farming.Agriculture.hoesDamageOnUseMultiplier - 1, event.getPlayer(), (player) -> player.sendBreakAnimation(event.getContext().getHand()));
			event.getPlayer().getCooldownTracker().setCooldown(stack.getItem(), cooldown);
		}

		private static boolean isHoeDisabled(Item item) {
			return item.getTags().contains(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "disabled_hoes"));
		}
	}
}
