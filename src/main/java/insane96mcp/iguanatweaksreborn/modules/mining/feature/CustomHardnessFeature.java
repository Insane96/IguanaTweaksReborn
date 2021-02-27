package insane96mcp.iguanatweaksreborn.modules.mining.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.modules.mining.classutils.BlockHardness;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as white and blacklist")
public class CustomHardnessFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> customHardnessConfig;

    private static final ArrayList<String> customHardnessDefault = Lists.newArrayList("minecraft:coal_ore,6", "minecraft:iron_ore,9.0", "minecraft:gold_ore,10.5", "minecraft:diamond_ore,18", "minecraft:ancient_debris,50", "minecraft:redstone_ore,12", "minecraft:lapis_ore,12", "minecraft:emerald_ore,18", "minecraft:nether_quartz_ore,6", "minecraft:nether_gold_ore,9", "minecraft:obsidian,40");

    public ArrayList<BlockHardness> customHardness;

    public CustomHardnessFeature(ITModule module) {
        super(module);
        
        Config.builder.comment(this.getDescription()).push(this.getName());
        customHardnessConfig = Config.builder
                .comment("Define custom blocks hardness, one string = one block/tag. Those blocks are not affected by the global block hardness multiplier.\nThe format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid\nE.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension.\nE.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.")
                .defineList("Custom Hardness", customHardnessDefault, o -> o instanceof String);
        Config.builder.pop();
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        customHardness = parseCustomHardnesses(this.customHardnessConfig.get());
    }

    public static ArrayList<BlockHardness> parseCustomHardnesses(List<? extends String> list) {
        ArrayList<BlockHardness> blockHardnesses = new ArrayList<>();
        for (String line : list) {
            BlockHardness blockHardness = BlockHardness.parseLine(line);
            if (blockHardness != null)
                blockHardnesses.add(blockHardness);
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
        double hardness = getBlockSingleHardness(block, dimensionId);
        if (hardness == -1d)
            return;
        double ratio = getRatio(hardness, blockState, world, pos);
        event.setNewSpeed(event.getNewSpeed() * (float) ratio);
    }

    private static double getRatio(double newHardness, BlockState state, World world, BlockPos pos) {
        //Add depth dimension multiplier
        double multiplier = 1d + Modules.miningModule.globalHardnessFeature.getDepthHardnessMultiplier(state.getBlock(), world.getDimensionKey().getLocation(), pos);
        return state.getBlockHardness(world, pos) / (newHardness * multiplier);
    }

    /**
     * Returns -1 when the block has no custom hardness, the hardness otherwise
     */
    public double getBlockSingleHardness(Block block, ResourceLocation dimensionId) {
        for (BlockHardness blockHardness : this.customHardness) {
            if (MCUtils.isInTagOrBlock(blockHardness, block, dimensionId)) {
                return blockHardness.hardness;
            }
        }
        return -1d;
    }
}
