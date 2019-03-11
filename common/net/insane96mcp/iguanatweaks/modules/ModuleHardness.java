package net.insane96mcp.iguanatweaks.modules;

import java.util.Set;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.init.ModConfig;
import net.insane96mcp.iguanatweaks.init.Strings;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ModuleHardness {
	public static void processHardness(BreakSpeed event) {
		processGlobalHardness(event);
		processSingleHardness(event);
	}
	
	public static final DamageSource BLOCK_BREAK = new DamageSource("block_break").setDamageBypassesArmor();
	
	public static void processWrongTool(BreakEvent event) {
		if (!ModConfig.Global.hardness.get())
			return;
		
		if (!ModConfig.Hardness.punishWrongTool.get())
			return;
		
		World world = event.getWorld().getWorld();
		
		if (world.isRemote)
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
		
		if (player.isCreative())
			return;
		
		IBlockState state = event.getState();
		Block block = state.getBlock();
		
		ItemStack mainHand = player.getHeldItemMainhand();

		Set<ToolType> mainHandTool = mainHand.getItem().getToolTypes(mainHand);
		
		ToolType harvestTool = block.getHarvestTool(state);
		int harvestLevel = block.getHarvestLevel(state);
		
		boolean isToolEffective = false;
		
		isToolEffective = harvestTool == null;
		if (!isToolEffective) {
			for (ToolType tool : mainHandTool) {
				if (block.isToolEffective(state, tool) && harvestLevel <= mainHand.getItem().getHarvestLevel(mainHand, tool, player, state)) {
					isToolEffective = true;
					break;
				}
			}
		}
		
		if (isToolEffective)
			return;
		
		event.setCanceled(true);
		
		ITextComponent textComponent;
		//TODO add back item destroying as config
		/*if (mainHand.isEmpty()) {
			
		}
		else {*/
		if (mainHand.isDamageable()) {
			if (mainHand.getMaxDamage() - mainHand.getDamage() > 1) {
				mainHand.damageItem(mainHand.getMaxDamage() - mainHand.getDamage() - 1, player);
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.6F, 0.8f);
			}
			else {
				mainHand.damageItem(1, player);
			}
		}
		else {
			player.attackEntityFrom(BLOCK_BREAK, (float) (state.getBlockHardness(event.getWorld(), event.getPos()) * ModConfig.Hardness.multiplier.get()));
			textComponent = new TextComponentTranslation(Strings.Translatable.Hardness.need_tool);
			/*player.renderBrokenItemStack(mainHand);
			mainHand.shrink(1);*/
		}
		textComponent = new TextComponentTranslation(Strings.Translatable.Hardness.wrong_tool);
		//}
		SPacketTitle title = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, textComponent);
		player.connection.sendPacket(title);
	}
	
	public static void processGlobalHardness(BreakSpeed event) {
		if (!ModConfig.Global.hardness.get())
			return;
		
		if (ModConfig.Hardness.multiplier.get() == 1.0f)
			return;
		
		World world = event.getEntityPlayer().world;
		
		IBlockState state = world.getBlockState(event.getPos());
		
		/*if (BetterWithMods.IsStumpOrRoot(state, world, event.getPos()))
			return;
		*/
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		boolean shouldProcess = true;
		
		if (ModConfig.Hardness.blacklist.get().size() > 0) {
			for (String line : ModConfig.Hardness.blacklist.get()) {
				if (line.trim().isEmpty())
					continue;
				
				String[] splitLine = line.split(":");
				if (splitLine.length < 2)
					continue;

				String modId = splitLine[0];
				String blockId = splitLine[1];
				ResourceLocation resourceLocation = new ResourceLocation(modId, blockId);
				
				if (blockResource.equals(resourceLocation)) {
					shouldProcess = false;
					break;
				}
			}
		}
		if (!shouldProcess)
			return;
		
		for (String line : ModConfig.Hardness.blacklist.get()) {
			//If is in blacklist mode
			if (!ModConfig.Hardness.blacklistIsWhitelist.get()){
				String[] splitLine = line.split(":");
				if (splitLine.length < 2)
					continue;
				String block = splitLine[0] + ":" + splitLine[1];
				if (block.equals(blockResource.toString())) {
					shouldProcess = false;
					break;
				}
			}
			//If is in whitelist mode
			else {
				shouldProcess = false;
				String[] splitLine = line.split(":");
				if (splitLine.length < 2)
					continue;
				String block = splitLine[0] + ":" + splitLine[1];
				
				if (block.equals(blockResource.toString())) {
					shouldProcess = true;
					break;
				}
			}
		}
		
		if (shouldProcess)
			event.setNewSpeed((float) (event.getOriginalSpeed() / ModConfig.Hardness.multiplier.get()));
	}
	
	public static void processSingleHardness(BreakSpeed event) {
		if (!ModConfig.Global.hardness.get())
			return;
		
		if (ModConfig.Hardness.blocksHardness.get().size() == 0)
			return;
		
		World world = event.getEntityPlayer().world;
		BlockPos pos = event.getPos();
		
		IBlockState state = event.getEntityPlayer().world.getBlockState(event.getPos());
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		
		if (ModConfig.Hardness.blocksHardness.get().size() > 0) {
			for (String line : ModConfig.Hardness.blocksHardness.get()) {
				if (line.trim().isEmpty())
					continue;
				
				String[] lineSplit = line.split(",");
				if (lineSplit.length != 2) {
					IguanaTweaks.logger.error("[BlocksHardness] Failed to parse line: " + line);
					continue;
				}
				
				String[] blockSplit = lineSplit[0].split(":");
				if (blockSplit.length != 2) {
					IguanaTweaks.logger.error("[BlockHardness] Failed to parse block id in: " + line);
					continue;
				}
				ResourceLocation blockId = new ResourceLocation(blockSplit[0], blockSplit[1]);
								
				float hardness = Float.parseFloat(lineSplit[1]);
				
				if (blockResource.equals(blockId)) {
					event.setNewSpeed(event.getOriginalSpeed() * getRatio(hardness, blockId, state, world, pos));
					break;
				}
			}
		}
	}
	
	private static float getRatio(float newHardness, ResourceLocation blockId, IBlockState state, World world, BlockPos pos) {
		Block block = ForgeRegistries.BLOCKS.getValue(blockId);
		float originalHardness = block.getDefaultState().getBlockHardness(world, pos);
		float ratio = originalHardness / newHardness;
		return ratio;
	}
}
