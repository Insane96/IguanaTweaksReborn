package insane96mcp.survivalreimagined.module.mining.block;

import insane96mcp.survivalreimagined.module.mining.entity.PrimedMiningCharge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MiningChargeBlock extends TntBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape SHAPE_Y = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    private static final VoxelShape SHAPE_X = Block.box(0.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D);
    private static final VoxelShape SHAPE_Z = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 6.0D);
    public MiningChargeBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.FALSE).setValue(FACING, Direction.DOWN));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        switch (direction) {
            case DOWN -> {
                return SHAPE_Y;
            }
            case UP -> {
                return SHAPE_Y.move(0, 10, 0);
            }
            case NORTH -> {
                return SHAPE_Z;
            }
            case SOUTH -> {
                return SHAPE_Z.move(0, 0, 10);
            }
            case WEST -> {
                return SHAPE_X;
            }
            case EAST -> {
                return SHAPE_X.move(10, 0, 0);
            }
        }
        return SHAPE_Y;
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            PrimedMiningCharge primedMiningCharge = new PrimedMiningCharge(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter, state.getValue(FACING));
            level.addFreshEntity(primedMiningCharge);
            level.playSound(null, primedMiningCharge.getX(), primedMiningCharge.getY(), primedMiningCharge.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.2F);
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        super.wasExploded(pLevel, pPos, pExplosion);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UNSTABLE, FACING);
    }
}
