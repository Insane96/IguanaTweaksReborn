package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.mobs.feature.StatsBuffs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.SpiderAttackGoal.class)
public abstract class SpiderAttackGoalMixin extends MeleeAttackGoal {

    public SpiderAttackGoalMixin(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
    }

    @Inject(at = @At(value = "RETURN"), method = "getAttackReachSqr", cancellable = true)
    private void onGetAttackReachSqr(LivingEntity pAttackTarget, CallbackInfoReturnable<Double> cir) {
        if (StatsBuffs.enableDataPack)
            cir.setReturnValue((double) (this.mob.getBbWidth() * 2f * this.mob.getBbWidth() * 2f + this.mob.getBbWidth()));
    }
}
