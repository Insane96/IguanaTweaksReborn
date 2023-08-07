package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Spider.class)
public abstract class SpiderMixin {
    /*@Inject(at = @At(value = "RETURN"), method = "getPassengersRidingOffset", cancellable = true)
    private void onGetAttackReachSqr(CallbackInfoReturnable<Double> cir) {
        if (StatsBuffs.enableDataPack)
            cir.setReturnValue((double) (((Spider)(Object)this).getBbHeight() * 1.4F));
    }*/
}
