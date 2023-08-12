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
        if ((dayTime < 12786 || dayTime >= 23216) && serverLevel.canSeeSky(pos) && serverLevel.getBrightness(LightLayer.SKY, pos) >= 10) {
            return super.isValidBonemealTarget(serverLevel, pos, state, isClient);
        }
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int light = level.getBrightness(LightLayer.SKY, pos);

        int dayTime = (int) (level.dayTime() % 24000);
        boolean isDayTime = dayTime < 12786 || dayTime >= 23216;
        boolean isRain = level.isRaining();
        boolean isThunder = level.isThundering();

        int oneInChanceToGrow = 10;
        if (!isDayTime) oneInChanceToGrow *= 2;
        if (!isRain) oneInChanceToGrow *= 2;
        if (!isThunder) oneInChanceToGrow *= 2;
        oneInChanceToGrow *= light / 15f;
        if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt(oneInChanceToGrow) == 0)) {
            super.performBonemeal(level, random, pos, state);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
        }
    }
}
