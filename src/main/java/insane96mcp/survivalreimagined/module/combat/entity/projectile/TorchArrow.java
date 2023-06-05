package insane96mcp.survivalreimagined.module.combat.entity.projectile;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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
        Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale(0.5F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        if (!this.level.isClientSide)
            this.level.setBlockAndUpdate(this.blockPosition(), Blocks.TORCH.getStateForPlacement(new BlockPlaceContext(this.level, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY, pResult)));
        this.discard();
    }
}
