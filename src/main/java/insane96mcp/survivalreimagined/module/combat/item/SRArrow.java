package insane96mcp.survivalreimagined.module.combat.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class SRArrow extends ArrowItem {
    final float baseDamage; // Vanilla arrow is 2.0
    final Supplier<EntityType<? extends Arrow>> arrowType;

    public SRArrow(Supplier<EntityType<? extends Arrow>> arrowType, float baseDamage, Properties pProperties) {
        super(pProperties);
        this.arrowType = arrowType;
        this.baseDamage = baseDamage;
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        Arrow arrow = arrowType.get().create(pLevel);
        if (arrow == null)
            return super.createArrow(pLevel, pStack, pShooter);
        arrow.setPos(pShooter.getX(), pShooter.getEyeY() - (double)0.1F, pShooter.getZ());
        arrow.setOwner(pShooter);
        if (pShooter instanceof Player) {
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        }
        arrow.setBaseDamage(baseDamage);
        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return false;
    }
}
