package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public class BowItemMixin {
    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "doubleValue=0.5", ordinal = 0))
    public double powerBonusPerLevel(double pBaseDamage) {
        return EnchantmentsFeature.powerEnchantmentDamage;
    }

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "CONSTANT", args = "doubleValue=0.5", ordinal = 1))
    public double powerBonusFlat(double pBaseDamage) {
        return EnchantmentsFeature.powerEnchantmentDamage != 0.5d ? 0 : pBaseDamage;
    }
}
