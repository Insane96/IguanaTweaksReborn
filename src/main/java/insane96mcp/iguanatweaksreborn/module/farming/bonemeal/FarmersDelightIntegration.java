package insane96mcp.iguanatweaksreborn.module.farming.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import vectorwing.farmersdelight.common.block.*;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.Optional;

public class FarmersDelightIntegration {
    public static boolean shouldBoneMeal(Level level, BlockPos pos, BlockState state) {
        return !state.is(ModBlocks.RICE_CROP_PANICLES.get());
    }

    public static void onBoneMeal(Level level, BlockPos pos, BlockState state, IntegerProperty ageProperty, int age) {
        if (state.getBlock() instanceof BuddingTomatoBlock buddingTomatoBlock) {
            int maxAge = buddingTomatoBlock.getMaxAge();
            if (age > maxAge) {
                int remainingGrowth = age - maxAge - 1;
                level.setBlockAndUpdate(pos, (ModBlocks.TOMATO_CROP.get()).defaultBlockState().setValue(TomatoVineBlock.VINE_AGE, remainingGrowth));
            }
        }
        else if (state.getBlock() instanceof TomatoVineBlock tomatoVineBlock) {
            tomatoVineBlock.attemptRopeClimb((ServerLevel) level, pos, level.random);
        }
        else if (state.getBlock() instanceof RiceBlock riceBlock) {
            int maxAge = riceBlock.getMaxAge();
            if (age > maxAge) {
                BlockState top = level.getBlockState(pos.above());
                if (top.getBlock() == ModBlocks.RICE_CROP_PANICLES.get()) {
                    BonemealableBlock growable = (BonemealableBlock)level.getBlockState(pos.above()).getBlock();
                    if (growable.isValidBonemealTarget(level, pos.above(), top, false)) {
                        growable.performBonemeal((ServerLevel) level, level.random, pos.above(), top);
                    }
                }
                else {
                    RicePaniclesBlock riceUpper = (RicePaniclesBlock)ModBlocks.RICE_CROP_PANICLES.get();
                    int remainingGrowth = age - maxAge - 1;
                    if (riceUpper.defaultBlockState().canSurvive(level, pos.above()) && level.isEmptyBlock(pos.above())) {
                        level.setBlockAndUpdate(pos, state.setValue(ageProperty, maxAge));
                        level.setBlock(pos.above(), riceUpper.defaultBlockState().setValue(RicePaniclesBlock.RICE_AGE, remainingGrowth), 2);
                    }
                }
            }
        }
    }

    public static Optional<IntegerProperty> getAgeProperty(BlockState state) {
        if (state.getBlock() instanceof BuddingBushBlock)
            return Optional.of(BuddingBushBlock.AGE);
        else
            return Optional.empty();
    }
}
