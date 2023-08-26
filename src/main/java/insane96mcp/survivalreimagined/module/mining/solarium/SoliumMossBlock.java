package insane96mcp.survivalreimagined.module.mining.solarium;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SoliumMossBlock extends GlowLichenBlock {
    public SoliumMossBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource randomSource, BlockPos pos, BlockState state) {
        if (randomSource.nextInt(5) == 0)
            super.performBonemeal(level, randomSource, pos, state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        if (!(level instanceof Level serverLevel))
            return false;
        int dayTime = (int) (serverLevel.dayTime() % 24000);
        if ((dayTime < 12786 || dayTime >= 23216) && serverLevel.getBrightness(LightLayer.SKY, pos) >= 10) {
            return super.isValidBonemealTarget(serverLevel, pos, state, isClient);
        }
        return false;
    }
}
