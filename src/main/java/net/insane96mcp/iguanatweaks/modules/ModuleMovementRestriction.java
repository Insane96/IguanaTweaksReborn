package net.insane96mcp.iguanatweaks.modules;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.capabilities.IPlayerData;
import net.insane96mcp.iguanatweaks.capabilities.PlayerDataProvider;
import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.insane96mcp.iguanatweaks.lib.Reflection;
import net.insane96mcp.iguanatweaks.lib.Strings;
import net.insane96mcp.iguanatweaks.modules.ModuleHardness.BlockMeta;
import net.insane96mcp.iguanatweaks.network.PacketHandler;
import net.insane96mcp.iguanatweaks.network.StunMessage;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class ModuleMovementRestriction {
	public static void ApplyPlayer(EntityLivingBase living) {
		if (!(living instanceof EntityPlayer))
			return;
		
		if (!ModConfig.config.global.movementRestriction){
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
		
		if (player.ticksExisted % ModConfig.config.misc.tickRatePlayerUpdate != 0)
			return;
		
		if (player.isCreative())
			return;
		
		float slownessDamage = SlownessDamage(player, world);
		float slownessWeight = SlownessWeight(player, world);
		float slownessTerrain = SlownessTerrain(player, world);
		
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	float speedModifierWeight = (100f - slownessWeight) / 100f;
    	float speedModifierDamage = (100f - slownessDamage) / 100f;
    	
    	float speedModifier = 1f - (speedModifierTerrain * speedModifierWeight * slownessDamage);
    	
    	if (player.moveForward < 0f && ModConfig.config.movementRestriction.slowdownWhenWalkingBackwards)
    		speedModifier = 0.5f + (speedModifier / 2f);

		AttributeModifier movSpeedModifier = new AttributeModifier(Utils.movSpeedRestrictionUUID, IguanaTweaks.RESOURCE_PREFIX + ":movSpeedMovementRestriction", -speedModifier, 1);
		IAttributeInstance movSpeedAttribute = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		if (movSpeedAttribute.getModifier(Utils.movSpeedRestrictionUUID) == null)
			movSpeedAttribute.applyModifier(movSpeedModifier);
		else if (movSpeedAttribute.getModifier(Utils.movSpeedRestrictionUUID).getAmount() != movSpeedModifier.getAmount()) {
			movSpeedAttribute.removeModifier(Utils.movSpeedRestrictionUUID);
			movSpeedAttribute.applyModifier(movSpeedModifier);
		}
		
		AttributeModifier swimSpeedModifier = new AttributeModifier(Utils.swimSpeedRestrictionUUID, IguanaTweaks.RESOURCE_PREFIX + ":swimSpeedMovementRestriction", -speedModifier / 2f, 1);
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

	//TODO Set tools and weapons weight based off material (or maybe attack speed and damage?)
	private static float GetStackWeight(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
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
			        toAdd += getItemWeight(stackInBox) * stackInBox.getCount();
				if (toAdd == 0f)
		        	toAdd = 1f / 64f * stack.getCount();
			}
			toAdd *= ModConfig.config.movementRestriction.shulkerWeightReduction;
			toAdd += getItemWeight(stack) * stack.getCount();
		}
		else if (!item.equals(Items.AIR)) {
	        toAdd = getItemWeight(stack) * stack.getCount();
		}
		
		return toAdd;
	}
	
	@SideOnly(Side.CLIENT)
	public static void RenderWeightTooltip(ItemTooltipEvent event) {
		if (!ModConfig.config.global.movementRestriction)
			return;
		
		if (ModConfig.config.movementRestriction.maxCarryWeight == 0)
			return;
		
		ItemStack stack = event.getItemStack();
		float weight = GetStackWeight(stack);
		if (weight > 0.0f) {
			DecimalFormat dFormat = new DecimalFormat("#.##");
			event.getToolTip().add(I18n.format(Strings.Translatable.MovementRestriction.weight) + ": " + dFormat.format(weight));
		}
		
		if (stack.getItem() instanceof ItemArmor) {
			ItemArmor item = (ItemArmor) stack.getItem();
			EntityPlayer player = event.getEntityPlayer();
			EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(stack);
			Collection<AttributeModifier> armorModifiers = item.getAttributeModifiers(slot, stack).get("generic.armor");
			float armor = 0f;
			for (AttributeModifier armorModifier : armorModifiers) {
				if (armorModifier.getOperation() == 0)
					armor += armorModifier.getAmount();
			}
			float weightWhenWorn = armor * ModConfig.config.movementRestriction.armorWeight;
			if (weightWhenWorn > 0.0f) {
				event.getToolTip().add(I18n.format(Strings.Translatable.MovementRestriction.weight_when_worn) + ": " + weightWhenWorn);
			}
		}
	}
	
	public static float SlownessWeight(EntityPlayer player, World world) {
		if (ModConfig.config.movementRestriction.maxCarryWeight == 0) 
			return 0f;

		//Has Weightless ring?
		boolean hasRing = false;
		for (ItemStack itemStack : player.inventory.mainInventory) {
			if (itemStack.getItem().getRegistryName().toString().equals("iguanatweaks:weightless_ring")) {
				setMaxCarryWeight(player, ModConfig.config.movementRestriction.maxCarryWeight * 2);
				hasRing = true;
				break;
			}
		}
		if (!hasRing)
			setMaxCarryWeight(player, ModConfig.config.movementRestriction.maxCarryWeight);
		
		float weight = 0f;
		
		float slownessWeight;
		
		for (ItemStack stack : player.inventory.mainInventory) 
		{
	        weight += GetStackWeight(stack);
		}
		for (ItemStack stack : player.inventory.offHandInventory) 
		{
	        weight += GetStackWeight(stack);
		}
		
		//Armor
		Iterable<ItemStack> armorInventory = player.getArmorInventoryList();
		for (ItemStack armorPiece : armorInventory) {
			EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(armorPiece);
			Collection<AttributeModifier> armorModifiers = armorPiece.getAttributeModifiers(slot).get("generic.armor");
			for (AttributeModifier armorModifier : armorModifiers) {
				if (armorModifier.getOperation() == 0)
					weight += armorModifier.getAmount() * ModConfig.config.movementRestriction.armorWeight;
			}
		}
		
		slownessWeight = (weight / getMaxCarryWeight(player)) * 100f;

    	if (slownessWeight > 0 && ModConfig.config.movementRestriction.encumbranceExhaustionPerSecond > 0f) {
    		float exhaustion = ModConfig.config.movementRestriction.encumbranceExhaustionPerSecond;
    		exhaustion *= (slownessWeight / 100f);
    		exhaustion /= (20 / ModConfig.config.misc.tickRatePlayerUpdate);
    		player.addExhaustion(exhaustion);
    	}
		
    	IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
    	
    	playerData.setWeight(weight);

		if (slownessWeight > 100f)
			slownessWeight = 100f;
		return slownessWeight;
	}
	
	public static float SlownessDamage(EntityPlayer player, World world) {
		if (ModConfig.config.movementRestriction.damageSlowdownDuration == 0)
			return 1f;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = playerData.getDamageSlownessDuration();
		
		if (duration == 0)
			return 1f;
		
		playerData.tickDamageSlownessDuration();
		
		return 1f - (ModConfig.config.movementRestriction.damageSlowdownEffectiveness / 100f);
	}
	
	public static void Stun(EntityLivingBase living, float damageAmount) {
		if (!ModConfig.config.global.movementRestriction)
			return;
		
		if (ModConfig.config.movementRestriction.damageSlowdownDuration == 0)
			return;
		
		if (!(living instanceof EntityPlayer))
			return;
		
		EntityPlayer player = (EntityPlayer)living;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		
		int duration = Math.round(damageAmount * 4);
		
		if (ModConfig.config.movementRestriction.damageSlowdownDifficultyScaling) {
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
		if (!ModConfig.config.global.movementRestriction)
			return;
		
    	if (living instanceof EntityPlayer || living.world.isRemote)
    		return;
    	
		World world = living.world;

		float speedModifier = 1f;
		
		if (ModConfig.config.misc.tickRateEntityUpdate == 0 || living.ticksExisted % ModConfig.config.misc.tickRateEntityUpdate != 0)
			return;
		
		float slownessTerrain = SlownessTerrain(living, world);
		
		float slownessArmor = living.getTotalArmorValue() * ModConfig.config.movementRestriction.armorWeightMobs;
		if (slownessArmor > 100f) 
			slownessArmor = 100f;
    	
    	float speedModifierArmor = (100f - slownessArmor) / 100f;
    	float speedModifierTerrain = (100f - slownessTerrain) / 100f;
    	
    	speedModifier = 1f - (speedModifierArmor * speedModifierTerrain);
    	
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
	
	public static float SlownessTerrain(EntityLivingBase living, World world) {
		if (living.isInWater() || living.isInLava() || !living.onGround || !ModConfig.config.movementRestriction.terrainSlowdown)
			return 0f;
		BlockPos entityPos = new BlockPos(living.posX, living.posY, living.posZ);

		return GetBlockSlowness(world, entityPos);
	}

	@SideOnly(Side.CLIENT)
	public static void PrintHudInfos(RenderGameOverlayEvent.Text event) {
		if (!ModConfig.config.global.movementRestriction)
			return;
		
		if (ModConfig.config.movementRestriction.maxCarryWeight == 0)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);
		float weight = playerData.getWeight();
		float encumbrance = weight / getMaxCarryWeight(player);

		if (!player.isDead && !player.capabilities.isCreativeMode && ModConfig.config.movementRestriction.addEncumbranceHudText)
		{
			TextFormatting color = TextFormatting.WHITE;
			
			String line = "";
			
			if (ModConfig.config.movementRestriction.detailedEncumbranceHudText)
			{
				if (encumbrance >= 0.95)
					color = TextFormatting.GRAY;
				else if (encumbrance >= 0.85)
					color = TextFormatting.RED;
				else if (encumbrance >= 0.60)
					color = TextFormatting.GOLD;
				else if (encumbrance >= 0.30)
					color = TextFormatting.YELLOW;
				else if (encumbrance >= 0.10)
					color = TextFormatting.GREEN;
				
				line = I18n.format(Strings.Translatable.MovementRestriction.weight) + ": " + Double.toString(Math.round(weight)) + " / " + Double.toString(Math.round(getMaxCarryWeight(player))) + " (" + String.format("%.2f", (weight / getMaxCarryWeight(player)) * 100) + "%)";
			}
			else
			{	
				if (encumbrance >= 0.95)
					color = TextFormatting.GRAY;
				else if (encumbrance >= 0.85)
					color = TextFormatting.RED;
				else if (encumbrance >= 0.60)
					color = TextFormatting.GOLD;
				else if (encumbrance >= 0.30)
					color = TextFormatting.YELLOW;
				else if (encumbrance >= 0.10)
					color = TextFormatting.GREEN;

				if (encumbrance >= 0.95)
					line = I18n.format(Strings.Translatable.MovementRestriction.fully_encumbered);
				else if (encumbrance >= 0.85)
					line = I18n.format(Strings.Translatable.MovementRestriction.almost_fully_encumbered);
				else if (encumbrance >= 0.60)
					line = I18n.format(Strings.Translatable.MovementRestriction.greatly_encumbered);
				else if (encumbrance >= 0.30)
					line = I18n.format(Strings.Translatable.MovementRestriction.encumbered);
				else if (encumbrance >= 0.10)
					line = I18n.format(Strings.Translatable.MovementRestriction.slightly_encumbered);
			}
			
			if (!line.isEmpty() && !mc.gameSettings.showDebugInfo) {
				if (ModConfig.config.movementRestriction.encumbranceTopLeft)
					event.getLeft().add(color + line + "\u00A7r");
				else 
					event.getRight().add(color + line + "\u00A7r");
			}

			if (mc.gameSettings.showDebugInfo && !mc.gameSettings.reducedDebugInfo && ModConfig.config.movementRestriction.addEncumbranceDebugText) {
				event.getLeft().add("");
				event.getLeft().add("[Iguana Tweaks] " + color + I18n.format(Strings.Translatable.MovementRestriction.weight) + ": " + String.format("%.2f", weight) + " / " + String.format("%d", getMaxCarryWeight(player)) + " (" + String.format("%.2f", encumbrance * 100.0f) + "%)");
			} 
		}
		
	}
	
	private static int getMaxCarryWeight(EntityPlayer player) {
		int maxCarryWeight = ModConfig.config.movementRestriction.maxCarryWeight;

		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);

		if (playerData.getMaxWeight() != 0)
			maxCarryWeight = playerData.getMaxWeight();

		return maxCarryWeight;
	}

	private static void setMaxCarryWeight(EntityPlayer player, int maxCarryWeight) {		
		IPlayerData playerData = player.getCapability(PlayerDataProvider.PLAYER_DATA_CAP, null);

		if (maxCarryWeight > 0)
			playerData.setMaxWeight(maxCarryWeight);
	}

	/////////////////////////////////////
	///Item Weights and Custom Weights///
	/////////////////////////////////////
	public static float getItemWeight(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int meta = itemStack.getMetadata();
		Block block = Block.getBlockFromItem(item);
		IBlockState state = block.getStateFromMeta(meta);
		
		for (CustomWeight customWeight : customWeights) {
			if (item.getRegistryName().equals(customWeight.blockMeta.id) && (meta == customWeight.blockMeta.meta || customWeight.blockMeta.meta == -1)) {
				return customWeight.weight;
			}
		}
		
		for (String line : ModConfig.config.movementRestriction.customWeight) {
			String[] lineSplit = line.split(",");
			if (lineSplit.length != 2)
				continue;
			
			String[] itemSplit = lineSplit[0].split(":");
			if (itemSplit.length < 2 || itemSplit.length > 3)
				continue;
			ResourceLocation itemId = new ResourceLocation(itemSplit[0], itemSplit[1]);
			
			int customMeta = -1;
			if (itemSplit.length == 3)
				customMeta = Integer.parseInt(itemSplit[2]);
			
			float weight = Float.parseFloat(lineSplit[1]);
			
		}

		return Utils.GetBlockWeight(block) * ModConfig.config.movementRestriction.rockWeight;
	}
	
	public static ArrayList<CustomWeight> customWeights = new ArrayList<>();
	
	public static void loadCustomWeights() {
		customWeights.clear();
		for (String line : ModConfig.config.movementRestriction.customWeight) {
        	if (line.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Custom Weights] Empty line found. Ignoring ...");
				continue;
        	}
        	
			String[] splitLine = line.split(",");
			if (splitLine.length != 2) {
				IguanaTweaks.logger.error("[Custom Weights] Failed to parse line: " + line + ", expected 2 arguments, got " + splitLine.length);
				continue;
			}
			
			String item = splitLine[0];
			BlockMeta blockMeta = Utils.parseBlock(item, "Custom Weights");
			if (blockMeta == null)
				return;
			
			float weight;
			try {
				weight = Float.parseFloat(splitLine[1]);
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[Custom Weights] Failed to parse weight for line: " + line);
				continue;
			}
			
			CustomWeight.addCustomWeight(new CustomWeight(blockMeta, weight));
		}
	}
	
	public static class CustomWeight {
		BlockMeta blockMeta;
		float weight;
		
		public CustomWeight(ResourceLocation id, int meta, float weight) {
			this.blockMeta = new BlockMeta(id, meta);
			this.weight = weight;
		}
		
		public CustomWeight(BlockMeta blockMeta, float weight) {
			this.blockMeta = blockMeta;
			this.weight = weight;
		}

		public static void addCustomWeight(CustomWeight c) {
			boolean contains = false;
			for (CustomWeight customWeight : customWeights) {
				if (customWeight.blockMeta.equals(c.blockMeta)) {
					contains = true;
					IguanaTweaks.logger.warn("[Custom Weights] Duplicated custom weight " + c.blockMeta);
					break;
				}
			}
			
			if (!contains)
				customWeights.add(c);
		}
	}

	/////////////////////////////
	///Custom Terrain Slowdown///
	/////////////////////////////

	public static float GetBlockSlowness(World world, BlockPos pos) {
		Material blockOnMaterial = world.getBlockState(pos.down()).getMaterial();			
		Material blockInMaterial = world.getBlockState(pos).getMaterial();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		int meta = block.getMetaFromState(state);
		
		float slowdown = -1f;
		
		for (CustomSlowdown customSlowdown : customTerrainSlowdowns) {
			if (block.getRegistryName().equals(customSlowdown.blockMeta.id) && (meta == customSlowdown.blockMeta.meta || customSlowdown.blockMeta.meta == -1)) {
				slowdown = customSlowdown.slowdown;
			}
		}
		
		if (slowdown == -1f) {
	        if (blockOnMaterial == Material.GRASS || blockOnMaterial == Material.GROUND) 
	        	slowdown = ModConfig.config.movementRestriction.terrainSlowdownOnDirt; 
	        else if (blockOnMaterial == Material.SAND) 
	        	slowdown = ModConfig.config.movementRestriction.terrainSlowdownOnSand;
	        else if (blockOnMaterial == Material.LEAVES || blockOnMaterial == Material.PLANTS || blockOnMaterial == Material.VINE) 
	        	slowdown = ModConfig.config.movementRestriction.terrainSlowdownOnPlant;
	        else if (blockOnMaterial == Material.ICE || blockOnMaterial == Material.PACKED_ICE)
	        	slowdown = ModConfig.config.movementRestriction.terrainSlowdownOnIce;
	        else if (blockOnMaterial == Material.SNOW || blockOnMaterial == Material.CRAFTED_SNOW)
	        	slowdown = ModConfig.config.movementRestriction.terrainSlowdownOnSnow;
	        else
	        	slowdown = 0f;
		}
        if (blockInMaterial == Material.SNOW || blockInMaterial == Material.CRAFTED_SNOW) 
        	slowdown += ModConfig.config.movementRestriction.terrainSlowdownInSnow;
		else if (blockInMaterial == Material.VINE || blockInMaterial == Material.PLANTS) 
			slowdown += ModConfig.config.movementRestriction.terrainSlowdownInPlant;

        if (slowdown > 100f)
        	slowdown = 100f;
        
        return slowdown;
	}
	
	public static void loadCustomTerrainSlowdown() {
		customWeights.clear();
		for (String line : ModConfig.config.movementRestriction.terrainSlowdownCustom) {
        	if (line.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Custom Terrain Slowdown] Empty line found. Ignoring ...");
				continue;
        	}
        	
        	String[] splitLine = line.split(",");
			if (splitLine.length != 2) {
				IguanaTweaks.logger.error("[Custom Terrain Slowdown] Failed to parse line: " + line + ", expected 2 arguments, got " + splitLine.length);
				continue;
			}
				
			String block = splitLine[0];
			BlockMeta blockMeta = Utils.parseBlock(block, "Custom Terrain Slowdown");
			if (blockMeta == null)
				return;
			
			float slowdown;
			try {
				slowdown = Float.parseFloat(splitLine[1]);
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[Custom Terrain Slowdown] Failed to parse slowness for line: " + line);
				continue;
			}
			
			CustomSlowdown.addCustomSlowdown(new CustomSlowdown(blockMeta, slowdown));
		}
	}
	
	public static ArrayList<CustomSlowdown> customTerrainSlowdowns = new ArrayList<>();
	
	public static class CustomSlowdown {
		BlockMeta blockMeta;
		float slowdown;
		
		public CustomSlowdown(ResourceLocation id, int meta, float slowdown) {
			this.blockMeta = new BlockMeta(id, meta);
			this.slowdown = slowdown;
		}
		
		public CustomSlowdown(BlockMeta blockMeta, float slowdown) {
			this.blockMeta = blockMeta;
			this.slowdown = slowdown;
		}

		public static void addCustomSlowdown(CustomSlowdown c) {
			boolean contains = false;
			for (CustomSlowdown customSlowdown : customTerrainSlowdowns) {
				if (customSlowdown.blockMeta.equals(c.blockMeta)) {
					contains = true;
					IguanaTweaks.logger.warn("[Custom Terrain Slowdown] Duplicated custom terrain slowdown " + c.blockMeta);
					break;
				}
			}
			
			if (!contains)
				customTerrainSlowdowns.add(c);
		}
	}
}
