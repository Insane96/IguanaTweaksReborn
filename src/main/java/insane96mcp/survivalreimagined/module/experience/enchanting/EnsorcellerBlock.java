package insane96mcp.survivalreimagined.module.experience.enchanting;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class EnsorcellerBlock extends BaseEntityBlock {

    public static final String CANNOT_MERGE_TAG = SurvivalReimagined.RESOURCE_PREFIX + "cannot_merge";
    public static final String CANNOT_BE_MERGED_LANG = SurvivalReimagined.MOD_ID + ".cannot_be_merged";
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected EnsorcellerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EnsorcellerBlockEntity(pPos, pState);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        else {
            this.openContainer(pLevel, pPos, pPlayer);
            return InteractionResult.CONSUME;
        }
    }

    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof EnsorcellerBlockEntity) {
            pPlayer.openMenu((MenuProvider)blockentity);
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof EnsorcellerBlockEntity ensorcellerBlockEntity) {
                if (pLevel instanceof ServerLevel) {
                    Containers.dropContents(pLevel, pPos, ensorcellerBlockEntity);
                }

                pLevel.updateNeighbourForOutputSignal(pPos, this);
                //ensorcellerBlockEntity.dropExperience(pLevel);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof EnsorcellerBlockEntity ensorcellerBlockEntity) {
            if (!level.isClientSide) {
                ItemStack itemstack = new ItemStack(this.asItem());
                //Clear items before dropping otherwise the item will stay in the dropped ensorceller
                ensorcellerBlockEntity.items.clear();
                ensorcellerBlockEntity.saveToItem(itemstack);
                if (ensorcellerBlockEntity.hasCustomName()) {
                    itemstack.setHoverName(ensorcellerBlockEntity.getCustomName());
                }

                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    /**
     * Called by BlockItem after this block has been placed.
     */
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof EnsorcellerBlockEntity) {
                ((EnsorcellerBlockEntity)blockentity).setCustomName(pStack.getHoverName());
            }
        }

    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles).
     */
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);

        int rollCost = EnsorcellerMenu.calculateRollCost(pLevel, pPos);
        if (rollCost <= 0)
            return;
        int invRollCost = 4 - rollCost;
        for (int i = 0; i < invRollCost; i++)
            pLevel.addParticle(new DustParticleOptions(new Vector3f(0.9882352941f, 0.8980392157f, 0.4392156863f), 5f), pPos.getX() + 0.5f + pRandom.nextFloat() * (2.5f + invRollCost) - ((2.5f + invRollCost) / 2f), pPos.getY() + pRandom.nextFloat() * (3.5f + invRollCost) + 1f, pPos.getZ() + 0.5f + pRandom.nextFloat() * (2.5f + invRollCost) - ((2.5f + invRollCost) / 2f), 0, 0, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter pLevel, List<Component> tooltip, TooltipFlag pFlag) {
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        int steps = 0;
        if (tag != null)
            steps = tag.getInt("Steps");
        tooltip.add(Component.literal("Levels rolled: %d".formatted(steps)));
    }
}
