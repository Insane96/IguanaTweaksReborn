package insane96mcp.iguanatweaksreborn.module.combat.fletching.entity.projectile;

import insane96mcp.iguanatweaksreborn.module.combat.fletching.Fletching;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
        if (this.level().isClientSide) {
            super.onHitBlock(pResult);
            return;
        }
        Direction direction = pResult.getDirection();
        BlockPos pos = pResult.getBlockPos().relative(direction);
        BlockState hitState = this.level().getBlockState(pos);
        BlockState stateToPlace = null;
        if (direction != Direction.UP) {
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(this.level(), null, InteractionHand.MAIN_HAND, getPickupItem(), pResult);
            stateToPlace = Blocks.WALL_TORCH.getStateForPlacement(blockPlaceContext);
        }
        else if (hitState.canBeReplaced()) {
            stateToPlace = Blocks.TORCH.defaultBlockState();
        }

        if (stateToPlace != null) {
            this.level().setBlock(pos, stateToPlace, 3);
            this.playSound(stateToPlace.getSoundType().getPlaceSound());
            this.discard();
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
