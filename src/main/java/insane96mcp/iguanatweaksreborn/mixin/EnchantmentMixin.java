package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "canApply(Lnet/minecraft/item/ItemStack;)Z", cancellable = true)
	private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		Enchantment enchantment = (Enchantment) (Object) this;
		if (Modules.combat.stats.disableEnchantment(enchantment))
			callback.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "canApplyAtEnchantingTable(Lnet/minecraft/item/ItemStack;)Z", cancellable = true, remap = false)
	private void canApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		Enchantment enchantment = (Enchantment) (Object) this;
		if (Modules.combat.stats.disableEnchantment(enchantment))
			callback.setReturnValue(false);
	}
}
