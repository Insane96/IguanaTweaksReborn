package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.insane96mcp.iguanatweaks.lib.Properties;
import net.insane96mcp.iguanatweaks.lib.Reflection;
import net.insane96mcp.iguanatweaks.lib.Utils;
import net.insane96mcp.iguanatweaks.network.PacketHandler;
import net.insane96mcp.iguanatweaks.network.StunMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ModuleMovementRestriction {
	public static void ApplyPlayer(EntityLivingBase living) {
		if (!(living instanceof EntityPlayer))
			return;
		
		if (!Properties.config.global.movementRestriction){
			IAttributeInstance movSpeedAttribute = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (movSpeedAttribute.getModifier(Utils.movSpeedRestrictionUUID) != null)
				movSpeedAttribute.removeModifier(Utils.movSpeedRestrictionUUID);
			
			IAttributeInstance swimSpeedAttribute = living.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
			if (swimSpeedAttribute.getModifier(Utils.swimSpeedRestrictionUUID) != null)
				swimSpeedAttribute.removeModifier(Utils.swimSpeedRestrictionUUID);
			return;
		}
		
		World world = living.world;
		
		EntityPlayer player = (EntityPlayer) living;
		
		if (player.ticksExisted % Properties.config.misc.tickRatePlayerUpdate != 0)
			return;
		
		if (player.isCreative())
			return;
		
		float slownessDamage = SlownessDamage(player, world);
		float slownessWeight = SlownessWeight(player, world);
		float slownessTerrain = SlownessTerrain(player, world);
		float slownessArmor = player.getTotalArmorValue() * Properties.config.movementRestriction.armorWeight;
		if (slownessArmor > 100f) 
			slownessArmor = 100f;
    	
    	float speedModifierArmour = (100f - slownessArmor) / 100f;
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	float speedModifierWeight = (100f - slownessWeight) / 100f;
    	float speedModifierDamage = (100f - slownessDamage) / 100f;
    	
    	float speedModifier = 1f - (speedModifierArmour * speedModifierTerrain * speedModifierWeight * slownessDamage);
    	
    	if (player.moveForward < 0f && Properties.config.movementRestriction.slowdownWhenWalkingBackwards)
    		speedModifier = 0.5f + (speedModifier / 2f);

		AttributeModifier movSpeedModifier = new AttributeModifier(Utils.movSpeedRestrictionUUID, IguanaTweaks.RESOURCE_PREFIX + ":movSpeedMovementRestriction", -speedModifier, 1);
		IAttributeInstance movSpeedAttribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (movSpeedAttribute.getModifier(Utils.movSpeedRestrictionUUID) == null)
			movSpeedAttribute.applyModifier(movSpeedModifier);
		else if (movSpeedAttribute.getModifier(Utils.movSpeedRestrictionUUID).getAmount() != movSpeedModifier.getAmount()) {
			movSpeedAttribute.removeModifier(Utils.movSpeedRestrictionUUID);
			movSpeedAttribute.applyModifier(movSpeedModifier);
		}
		
		AttributeModifier swimSpeedModifier = new AttributeModifier(Utils.swimSpeedRestrictionUUID, IguanaTweaks.RESOURCE_PREFIX + ":swimSpeedMovementRestriction", -speedModifier, 1);
		IAttributeInstance swimSpeedAttribute = player.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
		if (swimSpeedAttribute.getModifier(Utils.swimSpeedRestrictionUUID) == null)
			swimSpeedAttribute.applyModifier(swimSpeedModifier);
		else if (swimSpeedAttribute.getModifier(Utils.swimSpeedRestrictionUUID).getAmount() != swimSpeedModifier.getAmount()) {
			swimSpeedAttribute.removeModifier(Utils.swimSpeedRestrictionUUID);
			swimSpeedAttribute.applyModifier(swimSpeedModifier);
		}
		
		player.jumpMovementFactor = 0.02f * (1f - speedModifier);

		Reflection.Set(Reflection.EntityPlayer_speedInAir, player, 0.02f * (1f - speedModifier));
	}
	
	public static float SlownessWeight(EntityPlayer player, World world) {
		float weight = 0f;
		
		float slownessWeight;
		
		if (Properties.config.movementRestriction.maxCarryWeight == 0) 
			return 0f;
		
		for (ItemStack stack : player.inventory.mainInventory) 
		{
			if (stack.isEmpty())
				continue;
	        float toAdd = 0f;
	        
	        Item item = stack.getItem();
	        
			Block block = Block.getBlockFromItem(stack.getItem());
			//IBlockState state = block.getStateFromMeta(stack.getMetadata());
			
			if (block instanceof BlockShulkerBox) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt = stack.writeToNBT(nbt);
				NBTTagCompound blockEntityTag = nbt.getCompoundTag("tag").getCompoundTag("BlockEntityTag");
				NBTTagList items = blockEntityTag.getTagList("Items", 10);
				for (int i = 0; i < items.tagCount(); i++){
					NBTTagCompound itemTags = items.getCompoundTagAt(i);
					ItemStack stackInBox = new ItemStack(Item.getByNameOrId(itemTags.getString("id")), itemTags.getByte("Count"), itemTags.getShort("Damage"));
					Block blockInBox = Block.getBlockFromItem(stackInBox.getItem());
					if (!block.equals(Blocks.AIR) && !stack.getItem().equals(Items.AIR))	        
				        toAdd += Utils.GetItemWeight(stackInBox) * stackInBox.getCount();
					if (toAdd == 0f)
			        	toAdd = 1f / 64f * stack.getCount();
				}
				toAdd *= Properties.config.movementRestriction.shulkerWeightReduction;
			}
			else if (!item.equals(Items.AIR)) {
		        toAdd = Utils.GetItemWeight(stack) * stack.getCount();
			}
	        
	        weight += toAdd;
		}
		
		slownessWeight = (weight / Properties.config.movementRestriction.maxCarryWeight) * 100f;

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
		
		if (!player.onGround || Properties.config.movementRestriction.terrainSlowdownPercentage == 0)
			return 0f;
		BlockPos playerPos = new BlockPos(player.posX, player.posY - 1, player.posZ);
		
		slownessTerrain = Utils.GetBlockSlowness(world, playerPos);
        
        slownessTerrain = Math.round((float)slownessTerrain * ((float)Properties.config.movementRestriction.terrainSlowdownPercentage / 100f));
        
        if (slownessTerrain > 100f)
        	slownessTerrain = 100f;
        return slownessTerrain;
	}
	
	public static float SlownessDamage(EntityPlayer player, World world) {
		if (Properties.config.movementRestriction.damageSlowdownDuration == 0)
			return 1f;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = playerData.getDamageSlownessDuration();
		
		if (duration == 0)
			return 1f;
		
		playerData.tickDamageSlownessDuration();
		
		return 1f - (Properties.config.movementRestriction.damageSlowdownEffectiveness / 100f);
	}
	
	public static void Stun(EntityLivingBase living, float damageAmount) {
		if (!Properties.config.global.movementRestriction)
			return;
		
		if (Properties.config.movementRestriction.damageSlowdownDuration == 0)
			return;
		
		if (!(living instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)living;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = Math.round(damageAmount * 4);
		
		if (Properties.config.movementRestriction.damageSlowdownDifficultyScaling) {
			if (player.world.getDifficulty() == EnumDifficulty.EASY)
				duration *= 0.5;
			else if (player.world.getDifficulty() == EnumDifficulty.HARD)
				duration *= 2;
		}
		
		int playerDuration = playerData.getDamageSlownessDuration();
		
		playerData.setDamageSlownessDuration(duration + playerDuration);
    	
		PacketHandler.SendToClient(new StunMessage(duration + playerDuration), (EntityPlayerMP) player);
	}

	public static void ApplyEntity(EntityLivingBase living) {
		if (!Properties.config.global.movementRestriction)
			return;
		
    	if (living instanceof EntityPlayer || living.world.isRemote)
    		return;
    	
		World world = living.world;

		float speedModifier = 1f;
		
		if (living.ticksExisted % Properties.config.misc.tickRateEntityUpdate != 0)
			return;
		
		float slownessTerrain = SlownessTerrainEntity(living, world);
		
		float slownessArmor = living.getTotalArmorValue() * Properties.config.movementRestriction.armorWeight;
		if (slownessArmor > 100f) 
			slownessArmor = 100f;
    	
    	float speedModifierArmour = (100f - slownessArmor) / 100f;
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	
    	speedModifier = 1f - (speedModifierArmour * speedModifierTerrain);
    	
    	if (living.moveForward < 0f)
    		speedModifier = 0.5f + (speedModifier / 2f);

    	living.jumpMovementFactor = 0.02f * (1f - speedModifier);

		AttributeModifier modifier = new AttributeModifier(Utils.movSpeedRestrictionUUID, "movementRestriction", -speedModifier, 1);
		IAttributeInstance attribute = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (attribute.getModifier(Utils.movSpeedRestrictionUUID) == null)
			attribute.applyModifier(modifier);
		if (attribute.getModifier(Utils.movSpeedRestrictionUUID).getAmount() != modifier.getAmount())
			attribute.removeModifier(Utils.movSpeedRestrictionUUID);
	}
	
	public static float SlownessTerrainEntity(EntityLivingBase living, World world) {
		
		float slownessTerrain = 0f;
		
		if (!living.isInWater() || Properties.config.movementRestriction.terrainSlowdownPercentage == 0)
			return 0f;
		BlockPos playerPos = new BlockPos(living.posX, living.posY - 1, living.posZ);

		Material blockOnMaterial = world.getBlockState(playerPos).getMaterial();			
		Material blockInMaterial = world.getBlockState(playerPos.add(0, 1, 0)).getMaterial();
		
        if (blockOnMaterial == Material.GRASS || blockOnMaterial == Material.GROUND) 
        	slownessTerrain = Properties.config.movementRestriction.terrainSlowdownOnDirt; 
        else if (blockOnMaterial == Material.SAND) 
        	slownessTerrain = Properties.config.movementRestriction.terrainSlowdownOnSand;
        else if (blockOnMaterial == Material.LEAVES || blockOnMaterial == Material.PLANTS || blockOnMaterial == Material.VINE) 
        	slownessTerrain = Properties.config.movementRestriction.terrainSlowdownOnPlant;
        else if (blockOnMaterial == Material.ICE || blockOnMaterial == Material.PACKED_ICE)
        	slownessTerrain = Properties.config.movementRestriction.terrainSlowdownOnIce;
        else if (blockOnMaterial == Material.SNOW || blockOnMaterial == Material.CRAFTED_SNOW)
        	slownessTerrain = Properties.config.movementRestriction.terrainSlowdownOnSnow;
		
        if (blockInMaterial == Material.SNOW || blockInMaterial == Material.CRAFTED_SNOW) 
        	slownessTerrain += Properties.config.movementRestriction.terrainSlowdownInSnow;
		else if (blockInMaterial == Material.VINE || blockInMaterial == Material.PLANTS) 
			slownessTerrain += Properties.config.movementRestriction.terrainSlowdownInPlant;
        
        slownessTerrain = Math.round((float)slownessTerrain * ((float)Properties.config.movementRestriction.terrainSlowdownPercentage / 100f));
        
        if (slownessTerrain > 100f)
        	slownessTerrain = 100f;
        return slownessTerrain;
	}

	public static void PrintHudInfos(RenderGameOverlayEvent.Text event) {
		if (!Properties.config.global.movementRestriction)
			return;
		
		if (Properties.config.movementRestriction.maxCarryWeight > 0 || Properties.config.movementRestriction.armorWeight > 0d) 
		{
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			
			IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
			float weight = playerData.getWeight();
			float encumbrance = weight / Properties.config.movementRestriction.maxCarryWeight;

			if (mc.gameSettings.showDebugInfo && Properties.config.movementRestriction.addEncumbranceDebugText) {
				event.getLeft().add("");
				event.getLeft().add(I18n.format("info.weight") + ": " + String.format("%.2f", weight) + " / " + String.format("%d", Properties.config.movementRestriction.maxCarryWeight) + " (" + String.format("%.2f", encumbrance * 100.0f) + "%)");
			} 

			if (!player.isDead && !player.capabilities.isCreativeMode && Properties.config.movementRestriction.addEncumbranceHudText)
			{
				TextFormatting color = TextFormatting.WHITE;
				
				String line = "";
				
				if (Properties.config.movementRestriction.detailedEncumbranceHudText)
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
					
					line = I18n.format("info.weight") + ": " + Double.toString(Math.round(weight)) + " / " + Double.toString(Math.round(Properties.config.movementRestriction.maxCarryWeight)) + " (" + String.format("%.2f", (weight / Properties.config.movementRestriction.maxCarryWeight) * 100) + "%)";
				}
				else
				{
					float totalEncumberance = Math.max(encumbrance, player.getTotalArmorValue() * Properties.config.movementRestriction.armorWeight / 20f);
					
					if (totalEncumberance >= 0.95)
						color = TextFormatting.BOLD;
					else if (totalEncumberance >= 0.85)
						color = TextFormatting.GRAY;
					else if (totalEncumberance >= 0.40)
						color = TextFormatting.RED;
					else if (totalEncumberance >= 0.25)
						color = TextFormatting.GOLD;
					else if (totalEncumberance >= 0.10)
						color = TextFormatting.YELLOW;

					if (totalEncumberance >= 0.95)
						line = I18n.format("info.encumbered.fully");
					else if (totalEncumberance >= 0.85)
						line = I18n.format("info.encumbered.almost_fully");
					else if (totalEncumberance >= 0.40)
						line = I18n.format("info.encumbered.greatly");
					else if (totalEncumberance >= 0.25)
						line = I18n.format("info.encumbered.encumbered");
					else if (totalEncumberance >= 0.10)
						line = I18n.format("info.encumbered.slightly");
				}
				
				if (!line.isEmpty()) event.getRight().add(color + line + "\u00A7r");
			}
		}		
	}
}
