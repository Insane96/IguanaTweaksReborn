package insane96mcp.survivalreimagined.module.mining.solarium;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class SolariumBoudlerFeature extends Feature<BlockStateConfiguration> {

	public SolariumBoudlerFeature(Codec<BlockStateConfiguration> pCodec) {
		super(pCodec);
	}

	@Override
	public boolean place(FeaturePlaceContext<BlockStateConfiguration> context) {
		BlockPos blockpos = context.origin();
		WorldGenLevel worldgenlevel = context.level();
		RandomSource randomsource = context.random();

		BlockStateConfiguration blockstateconfiguration = context.config();
		for(; blockpos.getY() > worldgenlevel.getMinBuildHeight() + 3; blockpos = blockpos.below()) {
			if (!worldgenlevel.isEmptyBlock(blockpos.below())) {
				BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
				if (isDirt(blockstate) || blockstate.is(BlockTags.SAND)) {
					break;
				}
			}
		}

		if (blockpos.getY() <= worldgenlevel.getMinBuildHeight() + 3) {
			return false;
		} else {
			for(int l = 0; l < 3; ++l) {
				int i = randomsource.nextInt(3);
				int j = randomsource.nextInt(2) + 1;
				int k = randomsource.nextInt(3);
				float f = (float)(i + j + k) * 0.333F + 0.5F;

				for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, -j, -k), blockpos.offset(i, j, k))) {
					if (blockpos1.distSqr(blockpos) <= (double)(f * f)) {
						if (randomsource.nextBoolean())
							worldgenlevel.setBlock(blockpos1, Blocks.STONE.defaultBlockState, 3);
						else
							worldgenlevel.setBlock(blockpos1, Blocks.COBBLESTONE.defaultBlockState, 3);
						if (randomsource.nextFloat() < 0.4f)
							placeGrowthIfPossible(worldgenlevel, blockpos1);
					}
				}

				blockpos = blockpos.offset(-1 + randomsource.nextInt(2), -randomsource.nextInt(2), -1 + randomsource.nextInt(2));
			}

			return true;
		}
	}

	public static boolean placeGrowthIfPossible(WorldGenLevel pLevel, BlockPos pPos) {
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos().set(pPos);
		for (Direction direction : Direction.values()) {
			mutableBlockPos.set(pPos.relative(direction));
			BlockState curBlockState = pLevel.getBlockState(mutableBlockPos);
			if (!curBlockState.canBeReplaced())
				continue;
			BlockState blockStateToPlace = Solarium.SOLIUM_MOSS.block().get().defaultBlockState;
			for (Direction mossDirection : Direction.values()) {
				if (!MultifaceBlock.canAttachTo(pLevel, mossDirection, mutableBlockPos, pLevel.getBlockState(mutableBlockPos.relative(mossDirection)))) {
					blockStateToPlace.setValue(MultifaceBlock.getFaceProperty(mossDirection), false);
					continue;
				}
				blockStateToPlace = blockStateToPlace.setValue(MultifaceBlock.getFaceProperty(mossDirection), true);
			}
			pLevel.setBlock(mutableBlockPos, blockStateToPlace, 3);
			pLevel.getChunk(mutableBlockPos).markPosForPostprocessing(mutableBlockPos);
		}

		return true;
	}
}
