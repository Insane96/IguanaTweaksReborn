package insane96mcp.survivalreimagined.module.combat.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ExplosiveArrow extends Arrow {
    public ExplosiveArrow(EntityType<? extends Arrow> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ExplosiveArrow(Level pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    public ExplosiveArrow(Level pLevel, LivingEntity pShooter) {
        super(pLevel, pShooter);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.level.explode(this.getOwner() == null ? this : this.getOwner(), this.getX(), this.getY(), this.getZ(), 2.5f, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        this.level.explode(this.getOwner() == null ? this : this.getOwner(), this.getX(), this.getY(), this.getZ(), 2.5f, Level.ExplosionInteraction.NONE);
    }
}
