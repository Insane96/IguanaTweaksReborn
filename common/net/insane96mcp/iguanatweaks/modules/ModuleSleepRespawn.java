package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.init.ModConfig;
import net.insane96mcp.iguanatweaks.init.Strings;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

public class ModuleSleepRespawn {

	public static void processSpawn(EntityPlayer player) {
		if (!ModConfig.Global.sleepRespawn.get())
			return;
		
		if (ModConfig.SleepRespawn.spawnLocationRadiusMax.get() == 0)
			return;
		
		NBTTagCompound tags = player.getEntityData();
		boolean hasAlreadySpawned = tags.getBoolean("iguanatweaks:spawned");
        if (!hasAlreadySpawned)
        {
        	tags.putBoolean("iguanatweaks:spawned", true);
			respawnPlayer(player, ModConfig.SleepRespawn.spawnLocationRadiusMin.get(), ModConfig.SleepRespawn.spawnLocationRadiusMax.get());
        }
	}
	
	public static void processRespawn(EntityPlayer player) {
		if (!ModConfig.Global.sleepRespawn.get())
			return;
		
        respawnPlayer(player, ModConfig.SleepRespawn.respawnLocationRadiusMin.get(), ModConfig.SleepRespawn.respawnLocationRadiusMax.get());
        playerHealth(player);
        
        destroyBed(player);
        
        if (ModConfig.SleepRespawn.respawnLocationRadiusMax.get() != 0)
        	player.sendMessage(new TextComponentTranslation(Strings.Translatable.SleepRespawn.random_respawn));
	}
	
	private static void destroyBed(EntityPlayer player) {
		if (!ModConfig.SleepRespawn.destroyBedOnRespawn.get())
			return;
		
		BlockPos bedPos = player.getBedLocation(player.dimension);
		
		if (bedPos == null)
			return;
		
		World world = player.getEntityWorld();
		
        if (!world.getBlockState(bedPos).isBed(world, bedPos, player))
        	return;

        world.setBlockState(bedPos, Blocks.AIR.getDefaultState(), 3);
    	
		if (ModConfig.SleepRespawn.respawnLocationRadiusMax.get() == 0)
			player.sendMessage(new TextComponentTranslation(Strings.Translatable.SleepRespawn.bed_destroyed));
	}

	private static void respawnPlayer(EntityPlayer player, int minDistance, int maxDistance) {		
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
		
		BlockPos newCoords = randomiseCoordinates(world, x, z, minDistance, maxDistance);
		ChunkPos chunkPos = new ChunkPos(newCoords.getX() >> 4, newCoords.getZ() >> 4);
		//TODO Wtf is a Consumer<Chunk>?
		//worldserver.getChunkProvider().loadChunks(Arrays.asList(chunkPos), loadedChunkConsumer);
        //System.out.println(world.getBlockState(newCoords).isBlockNormalCube());
        while (world.getBlockState(newCoords).isBlockNormalCube()) {
        	newCoords = newCoords.add(0, 1, 0);
            //System.out.println(world.getBlockState(newCoords).isBlockNormalCube());
        }
		player.setLocationAndAngles(newCoords.getX() + .5f, newCoords.getY() + 1.1f, newCoords.getZ() + .5f, 0.0f, 0.0f);
        playerMP.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        
        //System.out.println(player.posX + " " + player.posY + " " + player.posZ);
	}
	
	private static BlockPos randomiseCoordinates(World world, int x, int z, int min, int max) {
		BlockPos newBlockPos = new BlockPos(x, 64, z);
	
		for (int attempt = 0; attempt < 50; attempt++)
		{
			int rngX, rngZ;
			
			rngX = MathHelper.nextInt(world.rand, min, max);  
			if (world.rand.nextBoolean()) 
				rngX *= -1;
			newBlockPos = newBlockPos.add(rngX, 0, 0);

			rngZ = MathHelper.nextInt(world.rand, min, max);  
			if (world.rand.nextBoolean()) 
				rngZ *= -1;
			newBlockPos = newBlockPos.add(0, 0, rngZ);
			
    		ResourceLocation actualBiome = world.getBiome(newBlockPos).getRegistryName();
    		
    		if (actualBiome.equals(Biomes.DEEP_OCEAN.getRegistryName()) 
        		|| actualBiome.equals(Biomes.OCEAN.getRegistryName())
        		|| actualBiome.equals(Biomes.RIVER.getRegistryName()))
    			continue;
			
			newBlockPos = Utils.getTopSolidOrLiquidBlock(world, newBlockPos);
			
			if (newBlockPos.getY() >= 0) 
			{
				IguanaTweaks.logger.info("Good spawn found at " + newBlockPos);
				break;
			}
		}

		return newBlockPos;
	}

	private static void playerHealth(EntityPlayer player) {
		int respawnHealth = ModConfig.SleepRespawn.respawnHealth.get();
		EnumDifficulty difficulty = player.getEntityWorld().getDifficulty();
		
		if (ModConfig.SleepRespawn.respawnHealthDifficultyScaling.get()) {
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

	public static void disabledSpawnPoint(PlayerSleepInBedEvent event) {
		if (!ModConfig.Global.sleepRespawn.get())
			return;
		
		if (!ModConfig.SleepRespawn.disableSleeping.get()) 
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		if (player.world.isDaytime())
			return;
		
		event.setResult(SleepResult.OTHER_PROBLEM);
		
		if (ModConfig.SleepRespawn.disableSetRespawnPoint.get()) {
			player.sendStatusMessage(new TextComponentTranslation(Strings.Translatable.SleepRespawn.bed_decoration), true);
		}
		else {
			player.setSpawnPoint(event.getPos(), false, player.dimension);
			player.sendStatusMessage(new TextComponentTranslation(Strings.Translatable.SleepRespawn.enjoy_the_night), true);
		}
	}
}
