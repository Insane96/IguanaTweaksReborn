package insane96mcp.iguanatweaksreborn.mixin;

import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Drowned.DrownedSwimUpGoal.class)
public class DrownedSwimUpGoalMixin {

	@ModifyConstant(method = "canUse", constant = @Constant(intValue = 2))
	public int getLevel(int constant) {
		return 1;
	}
}
