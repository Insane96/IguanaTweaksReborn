package insane96mcp.iguanatweaksreborn.module.farming.bonemeal;

import net.mehvahdjukaar.supplementaries.common.block.blocks.FlaxBlock;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SupplementariesIntegration {
    public static boolean shouldBoneMeal(Level level, BlockPos pos, BlockState state){
        if (!state.is(ModRegistry.FLAX.get()))
            return true;
        return state.getValue(FlaxBlock.HALF) == DoubleBlockHalf.LOWER;
    }

    public static void onBoneMeal(Level level, BlockPos pos, BlockState state, IntegerProperty ageProperty, int age) {
        if (state.getBlock() instanceof FlaxBlock flaxBlock) {
            if (state.getValue(FlaxBlock.HALF) == DoubleBlockHalf.UPPER)
                pos = pos.below();

            if (age >= 4)
                level.setBlock(pos.above(), flaxBlock.getStateForAge(age).setValue(FlaxBlock.HALF, DoubleBlockHalf.UPPER), 2);
        }
    }
}
