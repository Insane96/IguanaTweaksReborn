package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.enchantment.protection.IProtectionEnchantment;
import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
	@ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
	private boolean onCanEnchant(boolean original) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			return false;
		return original;
	}

	@ModifyReturnValue(method = "canApplyAtEnchantingTable", at = @At("RETURN"))
	private boolean onCanApplyAtEnchantingTable(boolean original) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			return false;
		return original;
	}

	@ModifyReturnValue(method = "isTradeable", at = @At("RETURN"))
	private boolean onIsTradeable(boolean original) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			return false;
		return original;
	}

	@ModifyReturnValue(method = "isDiscoverable", at = @At("RETURN"))
	private boolean onIsDiscoverable(boolean original) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			return false;
		return original;
	}

	@ModifyReturnValue(method = "isAllowedOnBooks", at = @At("RETURN"))
	private boolean onIsAllowedOnBooks(boolean original) {
		if (EnchantmentsFeature.isEnchantmentDisabled((Enchantment) (Object) this))
			return false;
		return original;
	}

	@ModifyReturnValue(method = "getMaxLevel", at = @At("RETURN"))
	private int onGetMaxLevel(int original) {
		//noinspection ConstantValue
		if (EnchantmentsFeature.isInfinityOverhaulEnabled() && ((Enchantment)(Object) this) instanceof ArrowInfiniteEnchantment)
			return 4;
		return original;
	}

	@ModifyReturnValue(method = "checkCompatibility", at = @At("RETURN"))
	private boolean onCheckCompatibility(boolean original, Enchantment other) {
		if (!EnchantmentsFeature.isThornsOverhaul())
			return original;
		if (((Enchantment)(Object) this) instanceof ThornsEnchantment) {
			if ((other instanceof ProtectionEnchantment protectionEnchantment && protectionEnchantment.type != ProtectionEnchantment.Type.FALL)
					|| other instanceof IProtectionEnchantment)
				return false;
		}
		return original;
	}
}