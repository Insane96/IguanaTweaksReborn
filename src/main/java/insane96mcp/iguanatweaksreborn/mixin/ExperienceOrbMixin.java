package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @WrapOperation(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getXpRepairRatio()F"))
    public float onRepairPlayerItems(ItemStack instance, Operation<Float> original) {
        if (Feature.isEnabled(EnchantmentsFeature.class) && EnchantmentsFeature.mendingNerf)
            return original.call(instance) / 2f;
        return original.call(instance);
    }
    @ModifyExpressionValue(method = "durabilityToXp", at = @At(value = "CONSTANT", args = "intValue=2"))
    public int onRepairPlayerItems(int original) {
        if (Feature.isEnabled(EnchantmentsFeature.class) && EnchantmentsFeature.mendingNerf)
            return 1;
        return original;
    }
}
