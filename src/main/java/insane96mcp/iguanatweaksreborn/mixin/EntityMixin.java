package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.feature.Misc;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At(value = "RETURN"), method = "fireImmune", cancellable = true)
    private void onLateTick(CallbackInfoReturnable<Boolean> cir) {
        if (Misc.isFireImmune((Entity) (Object) this))
            cir.setReturnValue(true);
    }
}
