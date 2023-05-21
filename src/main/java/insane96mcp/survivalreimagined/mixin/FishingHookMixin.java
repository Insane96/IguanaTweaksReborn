package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.world.feature.Seasons;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FishingHook.class)
public class FishingHookMixin {

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "catchingFish", ordinal = 0)
    private int onDiscardOnLand(int i) {
        if (Seasons.shouldSlowdownFishing(((FishingHook)(Object)this).level))
            return i - 1;
        return i;
    }
}
