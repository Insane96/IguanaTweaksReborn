package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.experience.enchantments.EnchantmentsFeature;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin {
    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    public void onRepairPlayerItems(Player pPlayer, int pRepairAmount, CallbackInfoReturnable<Integer> cir) {
        if (Feature.isEnabled(EnchantmentsFeature.class) && EnchantmentsFeature.mendingOverhaul)
            cir.setReturnValue(pRepairAmount);
    }
}
