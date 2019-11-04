package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.insane96mcp.iguanatweaks.lib.ModConfig.ConfigOptions.Farming.NerfedBonemeal;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class ModuleFarming {

	public static void NerfBonemeal(BonemealEvent event) {
		if (event.getWorld().isRemote)
			return;
		
		if (!ModConfig.config.global.farming)
			return;
		
		if (ModConfig.config.farming.nerfedBonemeal.equals(NerfedBonemeal.DISABLED))
			return;
		
		IBlockState state = event.getWorld().getBlockState(event.getPos());
		if (state.getBlock() instanceof BlockCrops) {
			int age = 0;
			int maxAge = 7;
			if (state.getBlock() instanceof BlockBeetroot) {
				age = state.getValue(BlockBeetroot.BEETROOT_AGE);
				maxAge = 3;
			}
			else if (state.getBlock().getRegistryName().toString().contains("harvestcraft")) {
				//age = state.getValue(BlockPamCrop.CROPS_AGE);
				//maxAge = BlockPamCrop.MATURE_AGE;
			}
			else
				age = state.getValue(BlockCrops.AGE);

			if (age == maxAge)
				return;

			if (ModConfig.config.farming.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
				age += MathHelper.getInt(event.getWorld().rand, 1, 2);
			else if (ModConfig.config.farming.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
				age++;
			
			if (age > maxAge)
				age = maxAge;

			if (state.getBlock() instanceof BlockBeetroot) {
				state = state.withProperty(BlockBeetroot.BEETROOT_AGE, age);
			}
			else if (state.getBlock().getRegistryName().toString().contains("harvestcraft")) {
				//state = state.withProperty(BlockPamCrop.CROPS_AGE, age);
			}
			else
				state = state.withProperty(BlockCrops.AGE, age);
			
			event.getWorld().setBlockState(event.getPos(), state);
			event.setResult(Result.ALLOW);
		}
	}

	public static void cropRequireWater(CropGrowEvent.Pre event) {
		if (!ModConfig.config.global.farming)
			return;
		
		if (!ModConfig.config.farming.cropRequireWater)
			return;
		
		World world = event.getWorld();
		IBlockState sustainState = world.getBlockState(event.getPos().down());
		if (sustainState.getBlock() instanceof BlockFarmland) {
			int moisture = sustainState.getValue(BlockFarmland.MOISTURE);
			
			if (moisture < 7)
				event.setResult(Result.DENY);
		}
	}

	/**
	 * Handles Crop Growth Speed Multiplier and NoSunlight Growth multiplier
	 */
	public static void cropGrowthSpeedMultiplier(CropGrowEvent.Post event) {
		if (!ModConfig.config.global.farming)
			return;
		
		if (ModConfig.config.farming.cropGrowthMultiplier == 1.0f && ModConfig.config.farming.noSunlightGrowthMultiplier == 1.0f)
			return;
		
		World world = event.getWorld();
		IBlockState state = event.getOriginalState();
		
		if (!(state.getBlock() instanceof BlockCrops))
			return;
		
		float chance = 1f / ModConfig.config.farming.cropGrowthMultiplier;
		
		int skyLight = world.getLightFor(EnumSkyBlock.SKY, event.getPos());
		if (skyLight < ModConfig.config.farming.minSunlight)
			chance *= 1f / ModConfig.config.farming.noSunlightGrowthMultiplier;
			
		if (ModConfig.config.farming.cropGrowthMultiplier == 0.0f)
			chance = -1f;
		
		if (skyLight < ModConfig.config.farming.minSunlight 
				&& ModConfig.config.farming.noSunlightGrowthMultiplier == 0.0f)
			chance = -1f;
		
		if (event.getWorld().rand.nextFloat() > chance) {
			world.setBlockState(event.getPos(), state);
		}
	}

	public static void reedsGrowthSpeedMultiplier(CropGrowEvent.Post event) {
		if (!ModConfig.config.global.farming)
			return;
		
		if (ModConfig.config.farming.reedsGrowthMultiplier == 1.0f)
			return;
		
		World world = event.getWorld();
		IBlockState state = event.getOriginalState();
		
		if (!(state.getBlock() instanceof BlockReed))
			return;
		
		float chance = 1f / ModConfig.config.farming.reedsGrowthMultiplier;
		
		if (ModConfig.config.farming.reedsGrowthMultiplier == 0.0f)
			chance = -1f;
		
		if (event.getWorld().rand.nextFloat() > chance) {
			world.setBlockState(event.getPos(), state);
		}
	}
	
	public static void cactusGrowthSpeedMultiplier(CropGrowEvent.Post event) {
		if (!ModConfig.config.global.farming)
			return;
		
		if (ModConfig.config.farming.cactusGrowthMultiplier == 1.0f)
			return;
		
		World world = event.getWorld();
		IBlockState state = event.getOriginalState();
		
		if (!(state.getBlock() instanceof BlockCactus))
			return;
		
		float chance = 1f / ModConfig.config.farming.cactusGrowthMultiplier;
		
		if (ModConfig.config.farming.cactusGrowthMultiplier == 0.0f)
			chance = -1f;
		
		if (event.getWorld().rand.nextFloat() > chance) {
			world.setBlockState(event.getPos(), state);
		}
	}
}
