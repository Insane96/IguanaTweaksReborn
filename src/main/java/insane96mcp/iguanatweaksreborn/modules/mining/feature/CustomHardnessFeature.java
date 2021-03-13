package insane96mcp.iguanatweaksreborn.modules.mining.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.modules.mining.classutils.BlockHardness;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist")
public class CustomHardnessFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customHardnessConfig;

	private static final ArrayList<String> customHardnessDefault = Lists.newArrayList("minecraft:coal_ore,6", "minecraft:iron_ore,9.0", "minecraft:gold_ore,10.5", "minecraft:diamond_ore,15", "minecraft:ancient_debris,50", "minecraft:redstone_ore,12", "minecraft:lapis_ore,12", "minecraft:emerald_ore,15", "minecraft:nether_quartz_ore,6", "minecraft:nether_gold_ore,9", "minecraft:obsidian,40");

	public ArrayList<BlockHardness> customHardness;

	public CustomHardnessFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		customHardnessConfig = Config.builder
				.comment("Define custom blocks hardness, one string = one block/tag. Those blocks are not affected by the global block hardness multiplier.\n" +
						"The format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid\n" +
						"E.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension.\n" +
						"E.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.\n" +
						"As of 2.4.0 this now works with blocks that instantly break too (e.g. Torches)")
				.defineList("Custom Hardness", customHardnessDefault, o -> o instanceof String);
		Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        resetHardness();
        customHardness = parseCustomHardnesses(this.customHardnessConfig.get());
    }

    public void resetHardness() {
        if (customHardness == null)
            return;
        //Reset the 0 hardness blocks
        for (BlockHardness blockHardness : this.customHardness) {
            List<Block> blocksToProcess = new ArrayList<>();
            if (blockHardness.id != null) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockHardness.id);
                if (block != null)
                    blocksToProcess.add(block);
            }
            else {
                ITag<Block> blockTag = BlockTags.getCollection().get(blockHardness.tag);
                blocksToProcess.addAll(blockTag.getAllElements());
            }
            for (Block block : blocksToProcess) {
                block.getStateContainer().getValidStates().forEach(blockState -> {
                    if (blockHardness.has0Hardness) {
                        blockState.hardness = 0f;
                    }
                });
            }
        }
    }

    public static ArrayList<BlockHardness> parseCustomHardnesses(List<? extends String> list) {
        ArrayList<BlockHardness> blockHardnesses = new ArrayList<>();
        for (String line : list) {
            BlockHardness blockHardness = BlockHardness.parseLine(line);
            if (blockHardness == null)
                continue;
            blockHardnesses.add(blockHardness);
            //If the block's hardness is 0 I replace the hardness
            List<Block> blocksToProcess = new ArrayList<>();
            if (blockHardness.id != null) {
                Block block = ForgeRegistries.BLOCKS.getValue(blockHardness.id);
                if (block != null)
                    blocksToProcess.add(block);
            }
            else {
                ITag<Block> blockTag = BlockTags.getCollection().get(blockHardness.tag);
                blocksToProcess.addAll(blockTag.getAllElements());
            }
            for (Block block : blocksToProcess) {
                block.getStateContainer().getValidStates().forEach(blockState -> {
                    if (blockState.hardness == 0f || blockHardness.has0Hardness) {
                        blockState.hardness = (float) blockHardness.hardness;
                        blockHardness.has0Hardness = true;
                    }
                });
            }
        }

        return blockHardnesses;
    }

    @SubscribeEvent
    public void processSingleHardness(PlayerEvent.BreakSpeed event) {
        if (!this.isEnabled())
            return;

        if (this.customHardness.size() == 0)
            return;
        World world = event.getPlayer().world;

        ResourceLocation dimensionId = world.getDimensionKey().getLocation();

        BlockPos pos = event.getPos();
        BlockState blockState = world.getBlockState(pos);

        Block block = blockState.getBlock();
        double customHardness = getBlockSingleHardness(block, dimensionId);
        if (customHardness == -1d)
            return;
        double ratio = getRatio(customHardness, blockState, world, pos);
        event.setNewSpeed(event.getNewSpeed() * (float) ratio);
    }

    private static double getRatio(double newHardness, BlockState state, World world, BlockPos pos) {
        //Add depth dimension multiplier
        double depthMultiplier = Modules.miningModule.globalHardnessFeature.getDepthHardnessMultiplier(state.getBlock(), world.getDimensionKey().getLocation(), pos, true);
        double ratio = state.getBlockHardness(world, pos) / newHardness;
        double multiplier = (1d / ratio) + depthMultiplier;
        return 1d / multiplier;
    }

    /**
     * Returns -1 when the block has no custom hardness, the hardness otherwise
     */
    public double getBlockSingleHardness(Block block, ResourceLocation dimensionId) {
        for (BlockHardness blockHardness : this.customHardness) {
            if (blockHardness.isInTagOrBlock(block, dimensionId)) {
                return blockHardness.hardness;
            }
        }
        return -1d;
    }
}
