package insane96mcp.iguanatweaksreborn.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Mob.class)
public class MobMixin {

	//Fixes mobs not dropping bonus experience if the drop chance of armor/held items is set higher than 1f
	@ModifyConstant(method = "getExperienceReward", constant = @Constant(floatValue = 1f))
	public float onDropChanceCheck(float dropChance) {
		return 100f;
	}
}
