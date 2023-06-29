package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.mining.feature.Gold;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Drowned.DrownedSwimUpGoal.class)
public class DrownedSwimUpGoalMixin {

	@ModifyConstant(method = "canUse", constant = @Constant(intValue = 2))
	public int getLevel(int constant) {
		return 1;
	}
}
