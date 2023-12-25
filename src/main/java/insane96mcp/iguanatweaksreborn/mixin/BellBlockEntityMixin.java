package insane96mcp.iguanatweaksreborn.mixin;

import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BellBlockEntity.class)
public class BellBlockEntityMixin {

	@ModifyConstant(method = "updateEntities", constant = @Constant(doubleValue = 48))
	private static double nearbyEntitiesRange(double range) {
		return 128d;
	}

	@ModifyConstant(method = "isRaiderWithinRange", constant = @Constant(doubleValue = 48))
	private static double nearbyRiderRange(double range) {
		return 128d;
	}
}