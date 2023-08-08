package insane96mcp.survivalreimagined.module.combat.fletching.dispenser;

import insane96mcp.survivalreimagined.module.combat.fletching.item.SRArrow;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SRArrowDispenseBehaviour extends AbstractProjectileDispenseBehavior {
	@Override
	protected Projectile getProjectile(Level pLevel, Position pPosition, ItemStack pStack) {
		return ((SRArrow)pStack.getItem()).createDispenserArrow(pLevel, pPosition, pStack);
	}
}
