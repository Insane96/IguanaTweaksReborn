package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection.IProtectionEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@Inject(at = @At("RETURN"), method = "canEnchant", cancellable = true)
	private void onCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "canApplyAtEnchantingTable", cancellable = true, remap = false)
	private void onCanApplyAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "isTradeable", cancellable = true, remap = false)
	private void onIsTradeable(CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "isDiscoverable", cancellable = true, remap = false)
	private void onIsDiscoverable(CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "isAllowedOnBooks", cancellable = true, remap = false)
	private void onIsAllowedOnBooks(CallbackInfoReturnable<Boolean> cir) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			cir.setReturnValue(false);
	}

	@Inject(at = @At("RETURN"), method = "getMaxLevel", cancellable = true)
	private void onGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
		//noinspection ConstantValue
		if (EnchantmentsFeature.isInfinityOverhaulEnabled() && ((Enchantment)(Object) this) instanceof ArrowInfiniteEnchantment)
			cir.setReturnValue(4);
	}

	@Inject(at = @At("RETURN"), method = "checkCompatibility", cancellable = true)
	private void onCheckCompatibility(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
		if (!EnchantmentsFeature.isThornsOverhaul())
			return;
		//noinspection ConstantValue
		if (((Enchantment)(Object) this) instanceof ThornsEnchantment) {
			if ((other instanceof ProtectionEnchantment protectionEnchantment && protectionEnchantment.type != ProtectionEnchantment.Type.FALL)
					|| other instanceof IProtectionEnchantment)
				cir.setReturnValue(false);
		}
	}
}