package insane96mcp.survivalreimagined.mixin;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.survivalreimagined.module.experience.feature.EnchantmentsFeature;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ThornsEnchantment.class)
public abstract class ThornsEnchantmentMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"), method = "doPostHurt")
    private void onPostHurtThorns(ItemStack stack, int amount, LivingEntity entity, Consumer<LivingEntity> pOnBroken) {
        if (Feature.isEnabled(EnchantmentsFeature.class) && EnchantmentsFeature.buffThorns)
            return;
        stack.hurtAndBreak(amount, entity, pOnBroken);
    }
}
