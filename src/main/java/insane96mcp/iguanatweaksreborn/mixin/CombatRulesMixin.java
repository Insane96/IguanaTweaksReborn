package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.combat.ArmorRework;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatRules.class)
public class CombatRulesMixin {
    @Inject(method = "getDamageAfterAbsorb", at = @At("HEAD"), cancellable = true)
    private static void onGetDamageAfterAbsorb(float damage, float armor, float toughness, CallbackInfoReturnable<Float> cir) {
        if (!Feature.isEnabled(ArmorRework.class))
            return;
        float calculatedDamage = ArmorRework.getCalculatedDamage(damage, armor, toughness);
        if (calculatedDamage == -1)
            return;
        cir.setReturnValue(calculatedDamage);
    }
}
