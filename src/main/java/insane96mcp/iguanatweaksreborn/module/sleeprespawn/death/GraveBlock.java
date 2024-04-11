package insane96mcp.iguanatweaksreborn.module.sleeprespawn.death;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GraveBlock extends BaseEntityBlock implements EntityBlock {
    protected static final VoxelShape SHAPE_X = Block.box(3.0D, 0.0D, 6.0D, 13.0D, 12.0D, 10.0d);
    protected static final VoxelShape SHAPE_Z = Block.box(6.0D, 0.0D, 3.0D, 10.0D, 12.0D, 13.0d);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

    public GraveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction direction = blockState.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos pos, BlockPos p_60546_) {
        if (state.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(state, direction, newState, levelAccessor, pos, p_60546_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48814_) {
        p_48814_.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveBlockEntity(pos, state);
    }

    public List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof GraveBlockEntity graveBlockEntity) {
            context = context.withDynamicDrop(CONTENTS, (consumer) -> {
                for(ItemStack itemStack : graveBlockEntity.getItems()) {
                    consumer.accept(itemStack);
                }
            });
        }

        return super.getDrops(state, context);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_60519_) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GraveBlockEntity graveBlockEntity)
                graveBlockEntity.getItems().forEach(itemStack -> dropGraveItems(level, itemStack, pos));

            super.onRemove(state, level, pos, newState, p_60519_);
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (!level.isClientSide
                || !(level.getBlockEntity(pos) instanceof GraveBlockEntity graveBlockEntity)
                || graveBlockEntity.getMessage() == null)
            return super.use(pState, level, pos, player, pHand, pHit);
        player.sendSystemMessage(Component.literal("This grave has a message: \"").append(graveBlockEntity.getMessage()).append(Component.literal("\"")));
        return InteractionResult.SUCCESS;
    }

    public void dropGraveItems(Level level, ItemStack stack, BlockPos pos) {
        if (stack.isEmpty())
            return;

        ItemEntity itementity = new ItemEntity(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, stack);
        //itementity.setPickUpDelay(40);
        //2 minutes
        itementity.lifespan = 2400;

        float f = level.random.nextFloat() * 0.25F;
        float f1 = level.random.nextFloat() * ((float)Math.PI * 2F);
        itementity.setDeltaMovement((-Mth.sin(f1) * f), 0.2F, (Mth.cos(f1) * f));
        level.addFreshEntity(itementity);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == Death.GRAVE_BLOCK_ENTITY_TYPE.get() && !level.isClientSide ? GraveBlockEntity::serverTick : null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        CompoundTag blockEntityData = BlockItem.getBlockEntityData(pStack);
        if (blockEntityData == null)
            return;
        pTooltip.add(Component.Serializer.fromJson(blockEntityData.getString("message")));
    }
}
