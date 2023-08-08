package insane96mcp.survivalreimagined.module.world.leaves;

import insane96mcp.insanelib.util.scheduled.ScheduledTickTask;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public class LeafDecayScheduledTick extends ScheduledTickTask {
    ServerLevel level;
    BlockPos pos;
    RandomSource randomSource;

    public LeafDecayScheduledTick(int tickDelay, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        super(tickDelay);
        this.level = level;
        this.pos = pos;
        this.randomSource = randomSource;
    }

    public void run() {
        if (this.level.isLoaded(this.pos))
            this.level.getBlockState(this.pos).randomTick(this.level, this.pos, this.randomSource);
    }
}
