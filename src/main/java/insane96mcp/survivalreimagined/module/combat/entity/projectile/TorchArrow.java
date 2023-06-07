package insane96mcp.survivalreimagined.module.combat.entity.projectile;

import insane96mcp.survivalreimagined.module.combat.feature.Fletching;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class TorchArrow extends Arrow {
    public TorchArrow(EntityType<? extends Arrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public TorchArrow(Level pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    public TorchArrow(Level pLevel, LivingEntity pShooter) {
        super(pLevel, pShooter);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if (this.level.isClientSide) {
            super.onHitBlock(pResult);
            return;
        }
        Direction direction = pResult.getDirection();
        BlockPos pos = pResult.getBlockPos().relative(direction);
        BlockState hitState = this.level.getBlockState(pos);
        if ((hitState.isAir() || hitState.canBeReplaced()) && direction != Direction.DOWN) {
            BlockState stateToPlace;
            if (direction == Direction.UP)
                stateToPlace = Blocks.TORCH.defaultBlockState();
            else
                stateToPlace = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction);
            if (stateToPlace.canSurvive(this.level, pos)) {
                this.level.setBlock(pos, stateToPlace, 2);
                this.playSound(stateToPlace.getSoundType().getPlaceSound());
                this.discard();
                return;
            }
        }
        super.onHitBlock(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        pResult.getEntity().setRemainingFireTicks(pResult.getEntity().getRemainingFireTicks() + 30);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Fletching.TORCH_ARROW_ITEM.get());
    }
}
