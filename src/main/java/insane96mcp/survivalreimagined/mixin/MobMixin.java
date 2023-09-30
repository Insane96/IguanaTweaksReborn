package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Mob.class)
public class MobMixin {

	@ModifyConstant(method = "getExperienceReward", constant = @Constant(floatValue = 1f))
	public float onDropChanceCheck(float dropChance) {
		return 100f;
	}
}
