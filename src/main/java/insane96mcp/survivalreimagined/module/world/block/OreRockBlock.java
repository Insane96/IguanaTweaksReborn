package insane96mcp.survivalreimagined.module.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OreRockBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);

    public OreRockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = blockState.getOffset(blockGetter, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public boolean canSurvive(BlockState p_49395_, LevelReader p_49396_, BlockPos p_49397_) {
        return p_49396_.getBlockState(p_49397_.below()).isFaceSturdy(p_49396_, p_49397_.below(), Direction.DOWN.getOpposite());
    }

    public BlockState updateShape(BlockState p_49029_, Direction p_49030_, BlockState p_49031_, LevelAccessor p_49032_, BlockPos p_49033_, BlockPos p_49034_) {
        return !p_49029_.canSurvive(p_49032_, p_49033_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_49029_, p_49030_, p_49031_, p_49032_, p_49033_, p_49034_);
    }
}
