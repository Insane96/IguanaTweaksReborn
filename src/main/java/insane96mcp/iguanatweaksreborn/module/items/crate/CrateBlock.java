package insane96mcp.iguanatweaksreborn.module.items.crate;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrateBlock extends BaseEntityBlock {

    public static final String WEIGHTED_LANG = IguanaTweaksReborn.MOD_ID + ".crate_weighted";

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");

    public CrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(OPEN, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_56198_) {
        return this.defaultBlockState().setValue(FACING, p_56198_.getClickedFace());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56249_) {
        p_56249_.add(FACING, OPEN);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CrateBlockEntity) {
                player.openMenu((CrateBlockEntity)blockentity);
                player.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void tick(BlockState p_220758_, ServerLevel p_220759_, BlockPos p_220760_, RandomSource p_220761_) {
        BlockEntity blockentity = p_220759_.getBlockEntity(p_220760_);
        if (blockentity instanceof CrateBlockEntity crateBlockEntity) {
            crateBlockEntity.recheckOpen();
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof CrateBlockEntity crateBlockEntity) {
            if (!level.isClientSide && player.isCreative() && !crateBlockEntity.isEmpty()) {
                ItemStack itemstack = new ItemStack(this.asItem());
                blockentity.saveToItem(itemstack);
                if (crateBlockEntity.hasCustomName()) {
                    itemstack.setHoverName(crateBlockEntity.getCustomName());
                }

                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            } else {
                crateBlockEntity.unpackLootTable(player);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    public List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof CrateBlockEntity crateBlockEntity) {
            context = context.withDynamicDrop(CONTENTS, (p_56218_) -> {
                for(int i = 0; i < crateBlockEntity.getContainerSize(); ++i) {
                    p_56218_.accept(crateBlockEntity.getItem(i));
                }
            });
        }

        return super.getDrops(state, context);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CrateBlockEntity) {
                ((CrateBlockEntity)blockentity).setCustomName(stack.getHoverName());
            }
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean p_56238_) {
        if (!state.is(state1.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CrateBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }

            super.onRemove(state, level, pos, state1, p_56238_);
        }
    }



    public boolean hasAnalogOutputSignal(BlockState p_49058_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState p_49065_, Level p_49066_, BlockPos p_49067_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_49066_.getBlockEntity(p_49067_));
    }

    public BlockState rotate(BlockState p_49085_, Rotation p_49086_) {
        return p_49085_.setValue(FACING, p_49086_.rotate(p_49085_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_49082_, Mirror p_49083_) {
        return p_49082_.rotate(p_49083_.getRotation(p_49082_.getValue(FACING)));
    }

    public PushReaction getPistonPushReaction(BlockState p_56265_) {
        return PushReaction.DESTROY;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, blockGetter, components, tooltipFlag);
        components.add(Component.translatable(WEIGHTED_LANG, Crate.slownessAtCrates).withStyle(ChatFormatting.RED));
    }
}
