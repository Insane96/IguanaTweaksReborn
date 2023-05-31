package insane96mcp.survivalreimagined.module.combat.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DamageArrowItem extends ArrowItem {
    final float baseDamage; // Vanilla arrow is 2.0

    public DamageArrowItem(float baseDamage, Properties pProperties) {
        super(pProperties);
        this.baseDamage = baseDamage;
    }

    @Override
    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter) {
        AbstractArrow arrow = super.createArrow(pLevel, pStack, pShooter);
        arrow.setBaseDamage(baseDamage);
        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return false;
    }
}
