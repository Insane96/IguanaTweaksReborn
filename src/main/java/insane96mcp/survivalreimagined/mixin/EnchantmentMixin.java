package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.combat.feature.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "canEnchant", cancellable = true)
	private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (Stats.disableEnchantment((Enchantment) (Object) this))
			callback.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "canApplyAtEnchantingTable", cancellable = true, remap = false)
	private void canApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (Stats.disableEnchantment((Enchantment) (Object) this))
			callback.setReturnValue(false);
	}
}