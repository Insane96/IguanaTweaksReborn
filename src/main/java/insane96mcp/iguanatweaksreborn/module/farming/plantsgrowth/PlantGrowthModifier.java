package insane96mcp.iguanatweaksreborn.module.farming.plantsgrowth;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PlantGrowthModifier {
    protected final float multiplier;

    protected PlantGrowthModifier(float multiplier) {
        this.multiplier = multiplier;
    }

    public float getMultiplier(BlockState state, Level level, BlockPos pos) {
        return this.multiplier;
    }
}
