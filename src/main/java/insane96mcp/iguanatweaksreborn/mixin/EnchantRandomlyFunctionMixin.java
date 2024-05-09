package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @ModifyArg(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/functions/EnchantRandomlyFunction;enchantItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;"))
    public Enchantment onGetEnchantment(Enchantment enchantment) {
        if (EnchantmentsFeature.shouldReplaceBaneOfArthropods(enchantment))
            return EnchantmentsFeature.BANE_OF_SSSSS.get();
        if (EnchantmentsFeature.shouldReplaceWithLuck(enchantment))
            return EnchantmentsFeature.LUCK.get();
        return enchantment;
    }
}
