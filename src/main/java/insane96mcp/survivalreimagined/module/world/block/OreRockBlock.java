package insane96mcp.survivalreimagined.module.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OreRockBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 3.0D, 11.0D);

    public OreRockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = blockState.getOffset(blockGetter, pos);
        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    public boolean canSurvive(BlockState p_49395_, LevelReader p_49396_, BlockPos p_49397_) {
        return canSupportRigidBlock(p_49396_, p_49397_.below());
    }
}
