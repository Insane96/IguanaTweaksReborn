package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "canEnchant", cancellable = true)
	private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (EnchantmentsFeature.disableEnchantment((Enchantment) (Object) this))
			callback.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "canApplyAtEnchantingTable", cancellable = true, remap = false)
	private void onCanApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (EnchantmentsFeature.disableEnchantment((Enchantment) (Object) this))
			callback.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "getMaxLevel", cancellable = true)
	private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
		//noinspection ConstantValue
		if (EnchantmentsFeature.isInfinityOverhaulEnabled() && ((Enchantment)(Object) this) instanceof ArrowInfiniteEnchantment)
			cir.setReturnValue(4);
	}
}