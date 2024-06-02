package insane96mcp.iguanatweaksreborn.module.misc.beaconconduit;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ITRBeaconBlock extends BaseEntityBlock implements BeaconBeamBlock {
    public ITRBeaconBlock(Properties pProperties) {
        super(pProperties);
    }

    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ITRBeaconBlockEntity(pPos, pState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BeaconConduit.BEACON_BLOCK_ENTITY_TYPE.get(), ITRBeaconBlockEntity::tick);
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide)
            return InteractionResult.SUCCESS;

        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof ITRBeaconBlockEntity itrBeaconBlockEntity) {
            pPlayer.openMenu(itrBeaconBlockEntity);
            pPlayer.awardStat(Stats.INTERACT_WITH_BEACON);
        }

        return InteractionResult.CONSUME;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link BlockStateBase#getRenderShape}
     * whenever possible. Implementing/overriding is fine.
     */
    @SuppressWarnings("deprecation")
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /**
     * Called by BlockItem after this block has been placed.
     */
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (!pStack.hasCustomHoverName())
            return;

        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof ITRBeaconBlockEntity itrBeaconBlockEntity) {
            itrBeaconBlockEntity.setCustomName(pStack.getHoverName());
        }
    }

    @Override
    public String getDescriptionId() {
        return "block.minecraft.beacon";
    }
}
