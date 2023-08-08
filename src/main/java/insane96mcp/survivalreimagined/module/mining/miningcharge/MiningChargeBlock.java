package insane96mcp.survivalreimagined.module.mining.miningcharge;

import insane96mcp.survivalreimagined.module.misc.explosionoverhaul.SRExplosion;
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
import net.minecraft.world.level.block.Blocks;
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
    private static final VoxelShape SHAPE_DOWN = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    private static final VoxelShape SHAPE_UP = Block.box(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 6.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(2.0D, 2.0D, 10.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(0.0D, 2.0D, 2.0D, 6.0D, 14.0D, 14.0D);
    private static final VoxelShape SHAPE_EAST = Block.box(10.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    public MiningChargeBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.FALSE).setValue(FACING, Direction.DOWN));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        switch (direction) {
            case DOWN -> {
                return SHAPE_DOWN;
            }
            case UP -> {
                return SHAPE_UP;
            }
            case NORTH -> {
                return SHAPE_NORTH;
            }
            case SOUTH -> {
                return SHAPE_SOUTH;
            }
            case WEST -> {
                return SHAPE_WEST;
            }
            case EAST -> {
                return SHAPE_EAST;
            }
        }
        return SHAPE_DOWN;
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            PrimedMiningCharge primedMiningCharge = new PrimedMiningCharge(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter, state.getValue(FACING));
            primedMiningCharge.getPersistentData().putFloat(SRExplosion.RAY_STRENGTH_MULTIPLIER_TAG, 0.075f);
            level.addFreshEntity(primedMiningCharge);
            level.playSound(null, primedMiningCharge.getX(), primedMiningCharge.getY(), primedMiningCharge.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.2F);
            level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace().getOpposite());
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            PrimedMiningCharge primedMiningCharge = new PrimedMiningCharge(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, explosion.getIndirectSourceEntity(), state.getValue(FACING));
            primedMiningCharge.getPersistentData().putFloat(SRExplosion.RAY_STRENGTH_MULTIPLIER_TAG, 0.09f);
            int fuse = primedMiningCharge.getFuse();
            primedMiningCharge.setFuse((short)(level.random.nextInt(fuse / 4) + fuse / 8));
            level.addFreshEntity(primedMiningCharge);
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(UNSTABLE, FACING);
    }
}
