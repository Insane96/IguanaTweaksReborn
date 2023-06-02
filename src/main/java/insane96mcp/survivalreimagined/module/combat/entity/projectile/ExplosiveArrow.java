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

    //TODO overwrite both to have more control
    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.discard();
        if (!this.level.isClientSide)
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 2f, Level.ExplosionInteraction.MOB);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        this.discard();
        if (!this.level.isClientSide)
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 2f, Level.ExplosionInteraction.MOB);
    }
}
