package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ApplyBonusCount.OreDrops.class)
public class ApplyBonusCountOreDropsMixin {
    @Inject(method = "calculateNewCount", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void onCalculateNewCount(RandomSource random, int originalCount, int enchantmentLvl, CallbackInfoReturnable<Integer> cir) {
        if (!Feature.isEnabled(EnchantmentsFeature.class)
                || !EnchantmentsFeature.nerfFortune)
            return;
        float chance = enchantmentLvl * 0.3f;

        cir.setReturnValue(originalCount * (MathHelper.getAmountWithDecimalChance(random, chance) + 1));
    }
}
