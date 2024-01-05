package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
	@ModifyConstant(method = "getDamageBonus",
			constant = @Constant(floatValue = 2.5f))
	private float getTier(float constant) {
		return EnchantmentsFeature.typeBasedDamageEnchantmentBonus.floatValue();
	}
}
