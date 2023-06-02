package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin extends Enchantment {

	@Shadow
	@Final
	public ProtectionEnchantment.Type type;

	protected ProtectionEnchantmentMixin(Rarity rarityIn, EnchantmentCategory category, EquipmentSlot[] slots) {
		super(rarityIn, category, slots);
	}

	@Override
	public boolean isTreasureOnly() {
		if (this.type == ProtectionEnchantment.Type.ALL && EnchantmentsFeature.protectionNerf == EnchantmentsFeature.ProtectionNerf.DISABLE)
			return true;
		return super.isTreasureOnly();
	}

	@Override
	public boolean isTradeable() {
		if (this.type == ProtectionEnchantment.Type.ALL && EnchantmentsFeature.protectionNerf == EnchantmentsFeature.ProtectionNerf.DISABLE)
			return false;
		return super.isTradeable();
	}

	@Override
	public boolean isDiscoverable() {
		if (this.type == ProtectionEnchantment.Type.ALL && EnchantmentsFeature.protectionNerf == EnchantmentsFeature.ProtectionNerf.DISABLE)
			return false;
		return super.isDiscoverable();
	}

	@Override
	public int getMaxLevel() {
		if (this.type == ProtectionEnchantment.Type.ALL && EnchantmentsFeature.protectionNerf == EnchantmentsFeature.ProtectionNerf.NERF)
			return 3;

		return 4;
	}

	@Inject(at = @At(value = "RETURN", ordinal = 3), method = "getDamageProtection", cancellable = true)
	public void onFallDamageProtection(int pLevel, DamageSource pSource, CallbackInfoReturnable<Integer> cir) {
		if (EnchantmentsFeature.isFeatherFallingBuffed())
			cir.setReturnValue(pLevel * 4);
	}
}