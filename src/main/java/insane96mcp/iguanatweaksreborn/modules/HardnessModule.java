package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class HardnessModule {
    public static void ProcessHardness(PlayerEvent.BreakSpeed event) {
        ProcessGlobalHardness(event);
        ProcessSingleHardness(event);
    }

    public static void ProcessGlobalHardness(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.Modules.hardness)
            return;

        if (ModConfig.Hardness.multiplier == 1.0d && ModConfig.Hardness.dimensionMultipliers.size() == 0)
            return;

        World world = event.getPlayer().world;
        Dimension dimension = world.getDimension();
        ResourceLocation dimensionId = dimension.getType().getRegistryName();

        BlockState blockState = world.getBlockState(event.getPos());

        Block block = blockState.getBlock();
        ResourceLocation blockId = block.getRegistryName();

        for (ModConfig.Hardness.BlockHardness blockHardness : ModConfig.Hardness.customHardness)
            if (isInTagOrBlock(blockHardness, block, blockId, dimensionId))
                return;

        boolean isInWhitelist = false;

        for (ModConfig.Hardness.CommonTagBlock blacklistEntry : ModConfig.Hardness.blacklist) {
            if (!ModConfig.Hardness.blacklistAsWhitelist) {
                if (isInTagOrBlock(blacklistEntry, block, blockId, dimensionId))
                    return;
            }
            else {
                isInWhitelist = false;
                if (isInTagOrBlock(blacklistEntry, block, blockId, dimensionId)) {
                    isInWhitelist = true;
                    break;
                }
            }
        }

        if (!isInWhitelist && ModConfig.Hardness.blacklistAsWhitelist)
            return;

        double multiplier = ModConfig.Hardness.multiplier;

        for (ModConfig.Hardness.DimensionMultiplier dimensionMultiplier : ModConfig.Hardness.dimensionMultipliers) {
            if (dimensionId.equals(dimensionMultiplier.dimension)) {
                multiplier = dimensionMultiplier.multiplier;
                break;
            }
        }

        event.setNewSpeed((float) (event.getNewSpeed() / multiplier));
    }

    public static void ProcessSingleHardness(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.Modules.hardness)
            return;

        if (ModConfig.Hardness.customHardness.size() == 0)
            return;

        World world = event.getPlayer().world;
        Dimension dimension = world.getDimension();
        ResourceLocation dimensionId = dimension.getType().getRegistryName();

        BlockPos pos = event.getPos();
        BlockState blockState = world.getBlockState(pos);

        Block block = blockState.getBlock();
        ResourceLocation blockId = block.getRegistryName();

        double hardness = -1;

        for (ModConfig.Hardness.BlockHardness blockHardness : ModConfig.Hardness.customHardness) {
            if (isInTagOrBlock(blockHardness, block, blockId, dimensionId)) {
                hardness = blockHardness.hardness;
                break;
            }
        }

        if (hardness == -1)
            return;

        event.setNewSpeed(event.getNewSpeed() * (float) getRatio(hardness, blockState, world, pos));
    }

    private static double getRatio(double newHardness, BlockState state, World world, BlockPos pos) {
        return state.getBlockHardness(world, pos) / newHardness;
    }

    private static boolean isInTagOrBlock(ModConfig.Hardness.CommonTagBlock commonTagBlock, Block block, ResourceLocation blockId, ResourceLocation dimensionId) {
        if (commonTagBlock.tag != null) {
            if (!BlockTags.getCollection().getRegisteredTags().contains(commonTagBlock.tag))
                return false;

            if (BlockTags.getCollection().get(commonTagBlock.tag).contains(block))
                if ((!commonTagBlock.dimension.equals(Utils.AnyRL) && commonTagBlock.dimension.equals(dimensionId)) || commonTagBlock.dimension.equals(Utils.AnyRL))
                    return true;
        }
        else {
            if (blockId.equals(commonTagBlock.block))
                if ((!commonTagBlock.dimension.equals(Utils.AnyRL) && commonTagBlock.dimension.equals(dimensionId)) || commonTagBlock.dimension.equals(Utils.AnyRL))
                    return true;
        }
        return false;
    }
}
