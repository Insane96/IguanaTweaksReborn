package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.lib.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModuleMovementRestriction {
	public static void ApplyPlayer(EntityLivingBase living) {
		if (!Properties.Global.movementRestriction)
			return;
		
		World world = living.world;

		float speedModifier = 1f;
		
		EntityPlayerMP player;
		
		if (!(living instanceof EntityPlayerMP)) 
			return;
	
		player = (EntityPlayerMP) living;
		if (player.isCreative())
			return;
		
		float slownessDamage = SlownessDamage(player, world);
		
		if (player.ticksExisted % Properties.General.tickRateEntityUpdate != 0)
			return;
		
		
		float slownessWeight = SlownessWeight(player, world);
		float slownessTerrain = SlownessTerrain(player, world);
		
		float slownessArmor = player.getTotalArmorValue() * Properties.MovementRestriction.armorWeight;
		if (slownessArmor > 100f) 
			slownessArmor = 100f;
    	
    	float speedModifierArmour = (100f - slownessArmor) / 100f;
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	float speedModifierWeight = (100f - slownessWeight) / 100f;
    	float speedModifierDamage = (100f - slownessDamage) / 100f;
    	
    	speedModifier = 1f - (speedModifierArmour * speedModifierTerrain * speedModifierWeight * slownessDamage);
    	
    	if (player.moveForward < 0f && Properties.MovementRestriction.slowdownWhenWalkingBackwards)
    		speedModifier = 0.5f + (speedModifier / 2f);
    	
    	player.jumpMovementFactor = 0.02f * (1f - speedModifier / 1.333f);

		AttributeModifier modifier = new AttributeModifier(Utils.movementRestrictionUUID, "movementRestriction", -speedModifier, 1);
		IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (attribute.getModifier(Utils.movementRestrictionUUID) == null)
			attribute.applyModifier(modifier);
		if (attribute.getModifier(Utils.movementRestrictionUUID).getAmount() != modifier.getAmount())
			attribute.removeModifier(Utils.movementRestrictionUUID);
	}
	
	public static float SlownessWeight(EntityPlayer player, World world) {
		float weight = 0f;
		
		float slownessWeight;
		
		if (Properties.MovementRestriction.maxCarryWeight == 0) 
			return 0f;
		
		for (ItemStack stack : player.inventory.mainInventory) 
		{
			if (stack.isEmpty())
				continue;
	        float toAdd = 0f;
	        
			Block block = Block.getBlockFromItem(stack.getItem());

	        if (!block.equals(Blocks.AIR))	        
		        toAdd = Utils.GetBlockWeight(block) * Properties.MovementRestriction.rockWeight;
	        else
	        	toAdd = 1f / 64f;
	        weight += toAdd * stack.getCount();
		}
		
		slownessWeight = (weight / Properties.MovementRestriction.maxCarryWeight) * 100f;

    	if (slownessWeight > 0)
    		player.addExhaustion(0.0001F * Math.round(slownessWeight));
		
    	IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
    	
    	playerData.setWeight(weight);
    	
		if (slownessWeight > 100f)
			slownessWeight = 100f;
		return slownessWeight;
	}
	
	public static float SlownessTerrain(EntityPlayer player, World world) {
		
		float slownessTerrain = 0f;
		
		if (player.isInWater() || Properties.MovementRestriction.terrainSlowdownPercentage == 0)
			return 0f;
		BlockPos playerPos = new BlockPos(player.posX, player.posY - 1, player.posZ);

		Material blockOnMaterial = world.getBlockState(playerPos).getMaterial();			
		Material blockInMaterial = world.getBlockState(playerPos.add(0, 1, 0)).getMaterial();
		
        if (blockOnMaterial == Material.GRASS || blockOnMaterial == Material.GROUND) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnDirt; 
        else if (blockOnMaterial == Material.SAND) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnSand;
        else if (blockOnMaterial == Material.LEAVES || blockOnMaterial == Material.PLANTS || blockOnMaterial == Material.VINE) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnPlant;
        else if (blockOnMaterial == Material.ICE || blockOnMaterial == Material.PACKED_ICE)
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnIce;
        else if (blockOnMaterial == Material.SNOW || blockOnMaterial == Material.CRAFTED_SNOW)
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnSnow;
		
        if (blockInMaterial == Material.SNOW || blockInMaterial == Material.CRAFTED_SNOW) 
        	slownessTerrain += Properties.MovementRestriction.terrainSlowdownInSnow;
		else if (blockInMaterial == Material.VINE || blockInMaterial == Material.PLANTS) 
			slownessTerrain += Properties.MovementRestriction.terrainSlowdownInPlant;
        
        slownessTerrain = Math.round((float)slownessTerrain * ((float)Properties.MovementRestriction.terrainSlowdownPercentage / 100f));
        
        if (slownessTerrain > 100f)
        	slownessTerrain = 100f;
        return slownessTerrain;
	}
	
	public static float SlownessDamage(EntityPlayer player, World world) {
		if (Properties.MovementRestriction.damageSlowdownDuration == 0)
			return 1f;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = playerData.getDamageSlownessDuration();
		
		if (duration == 0)
			return 1f;
		
		playerData.tickDamageSlownessDuration();
		
		return 1f - (Properties.MovementRestriction.damageSlowdownEffectiveness / 100f);
	}
	
	public static void Stun(EntityLivingBase living, float damageAmount) {
		if (!Properties.Global.movementRestriction)
			return;
		
		if (Properties.MovementRestriction.damageSlowdownDuration == 0)
			return;
		
		if (!(living instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)living;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = Math.round(damageAmount * 4);
		
		if (Properties.MovementRestriction.damageSlowdownDifficultyScaling) {
			if (player.world.getDifficulty() == EnumDifficulty.EASY)
				duration *= 0.5;
			else if (player.world.getDifficulty() == EnumDifficulty.HARD)
				duration *= 2;
		}
		
		int playerDuration = playerData.getDamageSlownessDuration();
		
		playerData.setDamageSlownessDuration(duration + playerDuration);
	}

	public static void ApplyEntity(EntityLivingBase living) {
		if (!Properties.Global.movementRestriction)
			return;
		
    	if (living instanceof EntityPlayer || living.world.isRemote)
    		return;
    	
		World world = living.world;

		float speedModifier = 1f;
		
		if (living.ticksExisted % Properties.General.tickRateEntityUpdate != 0)
			return;
		
		float slownessTerrain = SlownessTerrainEntity(living, world);
		
		float slownessArmor = living.getTotalArmorValue() * Properties.MovementRestriction.armorWeight;
		if (slownessArmor > 100f) 
			slownessArmor = 100f;
    	
    	float speedModifierArmour = (100f - slownessArmor) / 100f;
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	
    	speedModifier = 1f - (speedModifierArmour * speedModifierTerrain);
    	
    	if (living.moveForward < 0f)
    		speedModifier = 0.5f + (speedModifier / 2f);

    	living.jumpMovementFactor = 0.02f * (1f - speedModifier / 1.333f);

		AttributeModifier modifier = new AttributeModifier(Utils.movementRestrictionUUID, "movementRestriction", -speedModifier, 1);
		IAttributeInstance attribute = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (attribute.getModifier(Utils.movementRestrictionUUID) == null)
			attribute.applyModifier(modifier);
		if (attribute.getModifier(Utils.movementRestrictionUUID).getAmount() != modifier.getAmount())
			attribute.removeModifier(Utils.movementRestrictionUUID);
	}
	
	public static float SlownessTerrainEntity(EntityLivingBase living, World world) {
		
		float slownessTerrain = 0f;
		
		if (living.isInWater() || Properties.MovementRestriction.terrainSlowdownPercentage == 0)
			return 0f;
		BlockPos playerPos = new BlockPos(living.posX, living.posY - 1, living.posZ);

		Material blockOnMaterial = world.getBlockState(playerPos).getMaterial();			
		Material blockInMaterial = world.getBlockState(playerPos.add(0, 1, 0)).getMaterial();
		
        if (blockOnMaterial == Material.GRASS || blockOnMaterial == Material.GROUND) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnDirt; 
        else if (blockOnMaterial == Material.SAND) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnSand;
        else if (blockOnMaterial == Material.LEAVES || blockOnMaterial == Material.PLANTS || blockOnMaterial == Material.VINE) 
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnPlant;
        else if (blockOnMaterial == Material.ICE || blockOnMaterial == Material.PACKED_ICE)
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnIce;
        else if (blockOnMaterial == Material.SNOW || blockOnMaterial == Material.CRAFTED_SNOW)
        	slownessTerrain = Properties.MovementRestriction.terrainSlowdownOnSnow;
		
        if (blockInMaterial == Material.SNOW || blockInMaterial == Material.CRAFTED_SNOW) 
        	slownessTerrain += Properties.MovementRestriction.terrainSlowdownInSnow;
		else if (blockInMaterial == Material.VINE || blockInMaterial == Material.PLANTS) 
			slownessTerrain += Properties.MovementRestriction.terrainSlowdownInPlant;
        
        slownessTerrain = Math.round((float)slownessTerrain * ((float)Properties.MovementRestriction.terrainSlowdownPercentage / 100f));
        
        if (slownessTerrain > 100f)
        	slownessTerrain = 100f;
        return slownessTerrain;
	}

	public static void PrintHudInfos(RenderGameOverlayEvent.Text event) {
		if (!Properties.Global.movementRestriction)
			return;
		
		if (Properties.MovementRestriction.maxCarryWeight > 0 || Properties.MovementRestriction.armorWeight > 0d) 
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			
			if (Properties.Hud.showCreativeText && !mc.gameSettings.showDebugInfo && player.capabilities.isCreativeMode)
			{
				event.getLeft().add("Creative Mode");
			}
			IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
			float weight = playerData.getWeight();
			float encumbrance = weight / Properties.MovementRestriction.maxCarryWeight;

			if (mc.gameSettings.showDebugInfo && Properties.MovementRestriction.addEncumbranceDebugText) {
				event.getLeft().add("");
				event.getLeft().add("Weight: " + String.format("%.2f", weight) + " / " + String.format("%d", Properties.MovementRestriction.maxCarryWeight) + " (" + String.format("%.2f", encumbrance * 100.0f) + "%)");
			} 

			if (!player.isDead && !player.capabilities.isCreativeMode && Properties.MovementRestriction.addEncumbranceHudText)
			{
				TextFormatting color = TextFormatting.WHITE;
				
				String line = "";
				
				if (Properties.MovementRestriction.detailedEncumbranceHudText)
				{
					if (encumbrance >= 0.95) 
						color = TextFormatting.BOLD;
					else if (encumbrance >= 0.85) 
						color = TextFormatting.GRAY;
					else if (encumbrance >= 0.40) 
						color = TextFormatting.RED;
					else if (encumbrance >= 0.25) 
						color = TextFormatting.GOLD;
					else if (encumbrance >= 0.10) 
						color = TextFormatting.YELLOW;
					
					line = "Weight: " + Double.toString(Math.round(weight)) + " / " + Double.toString(Math.round(Properties.MovementRestriction.maxCarryWeight)) + " (" + String.format("%.2f", (weight / Properties.MovementRestriction.maxCarryWeight) * 100) + "%)";
				}
				else
				{
					float totalEncumberance = (encumbrance + (player.getTotalArmorValue() * Properties.MovementRestriction.armorWeight / 20f)) * 100f;
					if (totalEncumberance >= 95) 
						color = TextFormatting.BOLD;
					else if (totalEncumberance >= 85) 
						color = TextFormatting.GRAY;
					else if (totalEncumberance >= 40) 
						color = TextFormatting.RED;
					else if (totalEncumberance >= 25) 
						color = TextFormatting.GOLD;
					else if (totalEncumberance >= 10) 
						color = TextFormatting.YELLOW;

					if (totalEncumberance >= 95)
						line = "Fully encumbered";
					else if (totalEncumberance >= 85)
						line = "Almost Fully encumbered";
					else if (totalEncumberance >= 40)
						line = "Greatly encumbered";
					else if (totalEncumberance >= 25)
						line = "Encumbered";
					else if (totalEncumberance >= 10)
						line = "Slightly encumbered";
				}
				
				if (!line.equals("")) event.getRight().add(color + line + "\u00A7r");
			}
		}		
	}

}
