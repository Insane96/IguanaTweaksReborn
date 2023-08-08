package insane96mcp.survivalreimagined.mixin;

import insane96mcp.survivalreimagined.module.world.Seasons;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile {

    protected FishingHookMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyVariable(at = @At(value = "STORE", ordinal = 0), method = "catchingFish", ordinal = 0)
    private int onTimeUntilHookPerTick(int i) {
        if (Seasons.shouldSlowdownFishing(this.level()))
            return i - 1;
        return i;
    }
}
