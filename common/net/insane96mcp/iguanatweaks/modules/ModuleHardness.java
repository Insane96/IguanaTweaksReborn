package net.insane96mcp.iguanatweaks.modules;

import java.util.ArrayList;
import java.util.Set;

import net.insane96mcp.iguanatweaks.IguanaTweaks;
import net.insane96mcp.iguanatweaks.integration.BetterWithMods;
import net.insane96mcp.iguanatweaks.lib.ModConfig;
import net.insane96mcp.iguanatweaks.lib.Strings;
import net.insane96mcp.iguanatweaks.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class ModuleHardness {
	

	public static void ProcessHardness(BreakSpeed event) {
		ProcessGlobalHardness(event);
		ProcessSingleHardness(event);
	}
	
	public static final DamageSource BLOCK_BREAK = new DamageSource("block_break").setDamageBypassesArmor();
	
	public static void ProcessWrongTool(BreakEvent event) {
		if (!ModConfig.config.hardness.punishWrongTool)
			return;
		
		World world = event.getWorld();
		
		if (world.isRemote)
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
		
		if (player.isCreative())
			return;
		
		IBlockState state = event.getState();
		Block block = state.getBlock();
		BlockPos pos = event.getPos();
		
		ItemStack mainHand = player.getHeldItemMainhand();

		Set<String> mainHandTool = mainHand.getItem().getToolClasses(mainHand);
		
		String harvestTool = block.getHarvestTool(state);
		int harvestLevel = block.getHarvestLevel(state);
		
		boolean isToolEffective = false;
		
		isToolEffective = harvestTool == null;
		if (!isToolEffective) {
			for (String tool : mainHandTool) {
				if (block.isToolEffective(tool, state) && harvestLevel <= mainHand.getItem().getHarvestLevel(mainHand, tool, player, state)) {
					isToolEffective = true;
					break;
				}
			}
		}
		
		if (isToolEffective)
			return;
		
		event.setCanceled(true);
		
		ITextComponent textComponent;
		/*if (mainHand.isEmpty()) {
			
		}
		else {*/
		if (mainHand.isItemStackDamageable()) {
			//TODO Fix this for 1.13, remove +1
			mainHand.damageItem(mainHand.getMaxDamage() - mainHand.getItemDamage() + 1, player);
		}
		else {
			player.attackEntityFrom(BLOCK_BREAK, state.getBlockHardness(event.getWorld(), event.getPos()) * ModConfig.config.hardness.multiplier);
			textComponent = new TextComponentTranslation(Strings.Translatable.Hardness.need_tool);
			/*player.renderBrokenItemStack(mainHand);
			mainHand.shrink(1);*/
		}
		textComponent = new TextComponentTranslation(Strings.Translatable.Hardness.wrong_tool);
		//}
		SPacketTitle title = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, textComponent);
		player.connection.sendPacket(title);
	}
	
	public static void ProcessGlobalHardness(BreakSpeed event) {
		if (!ModConfig.config.global.hardness)
			return;
		
		if (ModConfig.config.hardness.multiplier == 1.0f && ModConfig.config.hardness.dimensionMultiplier.length == 0)
			return;
		
		World world = event.getEntityPlayer().world;
		
		IBlockState state = world.getBlockState(event.getPos());
		
		if (BetterWithMods.IsStumpOrRoot(state, world, event.getPos()))
			return;
		
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		int meta = state.getBlock().getMetaFromState(state);
		boolean shouldProcess = true;
		for (BlockHardness blockHardness : blockHardnesses) {
			if (blockResource.equals(blockHardness.blockMeta.id) && (meta == blockHardness.blockMeta.meta || blockHardness.blockMeta.meta == -1)) {
				shouldProcess = false;
				break;
			}
		}
		if (!shouldProcess)
			return;
		
		for (BlockMeta block : blockList) {
			//If is in blacklist mode
			if (!ModConfig.config.hardness.blockListIsWhitelist) {
				if (blockResource.equals(block.id) && (meta == block.meta || block.meta == -1)) {
					shouldProcess = false;
					break;
				}
			}
			//Else the list is in whitelist mode
			else {
				shouldProcess = false;
				if (blockResource.equals(block.id) && (meta == block.meta || block.meta == -1)) {
					shouldProcess = true;
					break;
				}
			}
		}
		
		if (!shouldProcess)
			return;
		
		float finalMultiplier = ModConfig.config.hardness.multiplier;
		
		for (DimensionList dim : dimensionList) {
			if (event.getEntityPlayer().dimension == dim.dimensionId) {
				finalMultiplier = dim.multiplier;
				break;
			}
		}
		
		if (shouldProcess)
			event.setNewSpeed(event.getNewSpeed() / finalMultiplier);
	}
	
	public static void ProcessSingleHardness(BreakSpeed event) {
		if (!ModConfig.config.global.hardness)
			return;
		
		if (blockHardnesses.size() == 0)
			return;
		
		World world = event.getEntityPlayer().world;
		BlockPos pos = event.getPos();
		
		IBlockState state = event.getEntityPlayer().world.getBlockState(event.getPos());
		ResourceLocation blockResource = state.getBlock().getRegistryName();
		int meta = state.getBlock().getMetaFromState(state);
		for (BlockHardness blockHardness : blockHardnesses) {
			if (blockResource.equals(blockHardness.blockMeta.id) && (meta == blockHardness.blockMeta.meta || blockHardness.blockMeta.meta == -1)) {
				event.setNewSpeed(event.getNewSpeed() * GetRatio(blockHardness.hardness, blockHardness.blockMeta.id, state, world, pos));
				break;
			}
		}
	}
	
	private static float GetRatio(float newHardness, ResourceLocation blockId, IBlockState state, World world, BlockPos pos) {
		float originalHardness = Block.getBlockFromName(blockId.toString()).getBlockHardness(state, world, pos);
		float ratio = originalHardness / newHardness;
		return ratio;
	}

	private static ArrayList<BlockHardness> blockHardnesses = new ArrayList<>();
	private static ArrayList<BlockMeta> blockList = new ArrayList<>();
	private static ArrayList<DimensionList> dimensionList = new ArrayList<>();
	
	public static void loadBlockHardnesses() {
		blockHardnesses.clear();
		for (String line : ModConfig.config.hardness.blockHardness) {
        	if (line.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Custom Block Hardness] Empty line found. Ignoring ...");
				continue;
        	}
        	//Split line
			String[] splitLine = line.split(",");
			if (splitLine.length != 2)
			{
				IguanaTweaks.logger.error("[Custom Block Hardness] Failed to parse line: " + line + ", expected 2 arguments, got " + splitLine.length);
				continue;
			}
			//Split block in modId blockId and meta
			String block = splitLine[0];
        	BlockMeta blockMeta = Utils.parseBlock(block, "Custom Block Hardness");
        	if (blockMeta == null) 
        		continue;
			
			float hardness;
			try {
				hardness = Float.parseFloat(splitLine[1]);
			} catch (Exception e) {
				IguanaTweaks.logger.error("[Custom Block Hardness] Failed to parse hardness for line: " + line);
				continue;
			}
			
			BlockHardness.addBlockHardness(new BlockHardness(blockMeta, hardness));
		}
	}
	
	public static class BlockHardness {
		public BlockMeta blockMeta;
		public float hardness;
		
		public BlockHardness(ResourceLocation id, int meta, float hardness) {
			this.blockMeta = new BlockMeta(id, meta);
			this.hardness = hardness;
		}
		
		public BlockHardness(BlockMeta blockMeta, float hardness) {
			this.blockMeta = blockMeta;
			this.hardness = hardness;
		}

		public static void addBlockHardness(BlockHardness b) {
			boolean contains = false;
			for (BlockHardness blockHardness : blockHardnesses) {
				if (blockHardness.blockMeta.id.equals(b.blockMeta.id) && blockHardness.blockMeta.meta == b.blockMeta.meta) {
					contains = true;
					IguanaTweaks.logger.warn("[Custom Block Hardness] Duplicated id for block " + b.blockMeta.id.toString() + ":" + b.blockMeta.meta);
					break;
				}
			}
			if (!contains)
				blockHardnesses.add(b);
		}
	}
	
	public static void loadBlockList() {
		blockList.clear();
        for (String line : ModConfig.config.hardness.blockList) {
        	if (line.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Block Black/Whitelist] Empty line found. Ignoring ...");
				continue;
        	}

        	BlockMeta blockMeta = Utils.parseBlock(line, "Block Black/Whitelist");
        	if (blockMeta == null) 
        		continue;
        	
        	BlockMeta.addBlockList(blockMeta);
		}
	}
	
	public static class BlockMeta {
		public ResourceLocation id;
		public int meta;
		
		public BlockMeta(ResourceLocation id, int meta) {
			this.id = id;
			this.meta = meta;
		}

		public static void addBlockList(BlockMeta b) {
			boolean contains = false;
			for (BlockMeta blockList : blockList) {
				if (blockList.id.equals(b.id) && blockList.meta == b.meta) {
					contains = true;
					IguanaTweaks.logger.warn("[Block Black/Whitelist] Duplicated id for block " + b);
					break;
				}
			}
			
			if (!contains)
				blockList.add(b);
		}
		
		@Override
		public String toString() {
			return this.id.toString() + ":" + meta;
		}
	}
	
	public static void loadDimensionMultipliers() {
		dimensionList.clear();
        for (String dimension : ModConfig.config.hardness.dimensionMultiplier) {
        	if (dimension.trim().isEmpty()) {
				IguanaTweaks.logger.warn("[Dimension Multiplier] Empty line found. Ignoring ...");
				continue;
        	}
			String[] split = dimension.split(",");
			if (split.length != 2) {
				IguanaTweaks.logger.error("[Dimension Multiplier] Failed to parse dimension multiplier line: " + dimension + ". Expected 2 arguments, got " + split.length);
				continue;
			}
			int dimensionId;
			float dimensionMultiplier;
			try {
				dimensionId = Integer.parseInt(split[0]);
				dimensionMultiplier = Float.parseFloat(split[1]);
			}
			catch (Exception e) {
				IguanaTweaks.logger.error("[Dimension Multiplier] Failed to parse numbers in dimension multiplier line: " + dimension);
				continue;
			}
			
			DimensionList.addDimension(new DimensionList(dimensionId, dimensionMultiplier));
		}
	}
	
	public static class DimensionList {
		public int dimensionId;
		public float multiplier;
		
		public DimensionList(int dimensionId, float multiplier) {
			this.dimensionId = dimensionId;
			this.multiplier = multiplier;
		}

		public static void addDimension(DimensionList d) {
			boolean contains = false;
			for (DimensionList dimension : dimensionList) {
				if (dimension.dimensionId == d.dimensionId) {
					contains = true;
					IguanaTweaks.logger.warn("[Dimension Multiplier] Duplicated dimension " + d.dimensionId);
					break;
				}
			}
			
			if (!contains)
				dimensionList.add(d);
		}
	}
}
