package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.mobs.feature.StatsBuffs;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class)
public abstract class SpiderMixin {
    @Inject(at = @At(value = "RETURN"), method = "getPassengersRidingOffset", cancellable = true)
    private void onGetAttackReachSqr(CallbackInfoReturnable<Double> cir) {
        if (StatsBuffs.enableDataPack)
            cir.setReturnValue((double) (((Spider)(Object)this).getBbHeight() * 1.4F));
    }
}
