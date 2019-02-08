package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class ModuleSleepRespawn {

	public static void ProcessSpawn(EntityPlayer player) {
		if (!Properties.config.global.sleepRespawn)
			return;
		
		if (Properties.config.sleepRespawn.spawnLocationRandomMax <= 0)
			return;
		
		NBTTagCompound tags = player.getEntityData();
		boolean hasAlreadySpawned = tags.getBoolean("iguanatweaks:spawned");
        if (!hasAlreadySpawned)
        {
        	tags.setBoolean("iguanatweaks:spawned", true);
			RespawnPlayer(player, Properties.config.sleepRespawn.spawnLocationRandomMin, Properties.config.sleepRespawn.spawnLocationRandomMax);
        }
	}
	
	public static void ProcessRespawn(EntityPlayer player) {
		if (!Properties.config.global.sleepRespawn)
			return;
		
        RespawnPlayer(player, Properties.config.sleepRespawn.respawnLocationRandomMin, Properties.config.sleepRespawn.respawnLocationRandomMax);
        PlayerHealth(player);
        
        DestroyBed(player);
        
        if (Properties.config.sleepRespawn.respawnLocationRandomMax != 0)
        	player.sendMessage(new TextComponentTranslation("sleep.random_respawn"));
	}
	
	private static void DestroyBed(EntityPlayer player) {
		if (!Properties.config.sleepRespawn.destroyBedOnRespawn)
			return;
		
		BlockPos bedPos = player.getBedLocation(player.dimension);
		
		if (bedPos == null)
			return;
		
		World world = player.getEntityWorld();
		
        if (!world.getBlockState(bedPos).getBlock().equals(Blocks.BED))
        	return;

        world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 3);
    	
		if (Properties.config.sleepRespawn.respawnLocationRandomMax == 0)
			player.sendMessage(new TextComponentTranslation("sleep.bed_destroyed"));
	}

	private static void RespawnPlayer(EntityPlayer player, int minDistance, int maxDistance) {		
		if (maxDistance <= 0)
			return;
		
		World world = player.getEntityWorld();
		if (world.isRemote)
			return;
		
		int x = (int)player.posX;
		if (x < 0) 
			--x;
		
		int z = (int)player.posZ;
		if (z < 0) 
			--z;
		
		EntityPlayerMP playerMP = (EntityPlayerMP)player;
		
        WorldServer worldserver = playerMP.getServerWorld();
		
		BlockPos newCoords = RandomiseCoordinates(world, x, z, minDistance, maxDistance);
        worldserver.getChunkProvider().loadChunk(newCoords.getX() >> 4, newCoords.getZ() >> 4);
        System.out.println(world.getBlockState(newCoords).isBlockNormalCube());
        while (world.getBlockState(newCoords).isBlockNormalCube()) {
        	newCoords = newCoords.add(0, 1, 0);
            System.out.println(world.getBlockState(newCoords).isBlockNormalCube());
        }
		player.setLocationAndAngles(newCoords.getX() + .5f, newCoords.getY() + 1.1f, newCoords.getZ() + .5f, 0.0f, 0.0f);
        playerMP.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        
        System.out.println(player.posX + " " + player.posY + " " + player.posZ);
	}
	
	private static BlockPos RandomiseCoordinates(World world, int x, int z, int min, int max) {
		BlockPos newBlockPos = new BlockPos(x, 64, z);
	
		for (int attempt = 0; attempt < 50; attempt++)
		{
			int rngX, rngY, rngZ;
			
			rngX = MathHelper.getInt(world.rand, min, max);  
			if (world.rand.nextBoolean()) 
				rngX *= -1;
			newBlockPos = newBlockPos.add(rngX, 0, 0);

			rngZ = MathHelper.getInt(world.rand, min, max);  
			if (world.rand.nextBoolean()) 
				rngZ *= -1;
			newBlockPos = newBlockPos.add(0, 0, rngZ);
			
    		ResourceLocation actualBiome = world.getBiome(newBlockPos).getRegistryName();
    		
    		if (actualBiome.equals(Biomes.DEEP_OCEAN.getRegistryName()) 
        		|| actualBiome.equals(Biomes.OCEAN.getRegistryName())
        		|| actualBiome.equals(Biomes.RIVER.getRegistryName()))
    			continue;
			
			newBlockPos = world.getTopSolidOrLiquidBlock(newBlockPos);
			
			if (newBlockPos.getY() >= 0) 
			{
				IguanaTweaks.logger.info("Good spawn found at " + newBlockPos);
				break;
			}
		}

		return newBlockPos;
	}

	private static void PlayerHealth(EntityPlayer player) {
		int respawnHealth = Properties.config.sleepRespawn.respawnHealth;
		EnumDifficulty difficulty = player.getEntityWorld().getDifficulty();
		
		if (Properties.config.sleepRespawn.respawnHealthDifficultyScaling) {
			if (difficulty == EnumDifficulty.HARD) 
			{
				respawnHealth = (int) Math.max(respawnHealth / 2f, 1);
			}
			else if (difficulty.getId() <= EnumDifficulty.EASY.getId()) 
			{
				respawnHealth = (int) Math.min(respawnHealth * 2f, 20);
			}
		}
		
		if (difficulty == EnumDifficulty.PEACEFUL)
			respawnHealth = (int) player.getMaxHealth();

		player.setHealth(respawnHealth);
	}

	public static void DisabledSpawnPoint(PlayerSleepInBedEvent event) {
		if (!Properties.config.global.sleepRespawn)
			return;
		
		if (!Properties.config.sleepRespawn.disableSleeping) 
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		if (player.world.isDaytime())
			return;
		
		event.setResult(SleepResult.OTHER_PROBLEM);
		
		if (Properties.config.sleepRespawn.disableSetRespawnPoint) {
			player.sendStatusMessage(new TextComponentTranslation("sleep.bed_decoration"), true);
		}
		else {
			player.setSpawnChunk(event.getPos(), false, player.dimension);
			player.sendStatusMessage(new TextComponentTranslation("sleep.enjoy_the_night"), true);
		}
	}
}
