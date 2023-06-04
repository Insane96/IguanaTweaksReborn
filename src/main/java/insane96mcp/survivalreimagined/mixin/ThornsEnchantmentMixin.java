package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThornsEnchantment.class)
public abstract class ThornsEnchantmentMixin {
    /*@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V"), method = "doPostHurt")
    private void onCritArrowCheck(int amount, LivingEntity entity, Consumer<LivingEntity> pOnBroken) {

    }*/
}
