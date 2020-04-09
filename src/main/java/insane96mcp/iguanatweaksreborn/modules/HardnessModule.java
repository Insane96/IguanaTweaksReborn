package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class HardnessModule {
    public static void ProcessHardness(PlayerEvent.BreakSpeed event) {
        processGlobalHardness(event);
        processSingleHardness(event);
    }

    public static void processGlobalHardness(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.Modules.hardness)
            return;
        if (ModConfig.Hardness.multiplier == 1.0d && ModConfig.Hardness.dimensionMultipliers.size() == 0)
            return;
        World world = event.getPlayer().world;
        Dimension dimension = world.getDimension();
        ResourceLocation dimensionId = dimension.getType().getRegistryName();
        if (dimensionId == null)
            dimensionId = Utils.AnyRL;
        BlockState blockState = world.getBlockState(event.getPos());
        Block block = blockState.getBlock();
        for (ModConfig.Hardness.BlockHardness blockHardness : ModConfig.Hardness.customHardness)
            if (Utils.isInTagOrBlock(blockHardness, block, dimensionId))
                return;
        boolean isInWhitelist = false;
        for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.Hardness.blacklist) {
            if (!ModConfig.Hardness.blacklistAsWhitelist) {
                if (Utils.isInTagOrBlock(blacklistEntry, block, dimensionId))
                    return;
            }
            else {
                if (Utils.isInTagOrBlock(blacklistEntry, block, dimensionId)) {
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

    public static void processSingleHardness(PlayerEvent.BreakSpeed event) {
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

        double hardness = -1;

        for (ModConfig.Hardness.BlockHardness blockHardness : ModConfig.Hardness.customHardness) {
            if (Utils.isInTagOrBlock(blockHardness, block, dimensionId)) {
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

}
