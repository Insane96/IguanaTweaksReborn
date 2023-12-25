package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.Nerfs;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

    @Inject(method = "tick", at = {
            @At("HEAD"),
            @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.AFTER)
    }, cancellable = true)
    public void stopTickingIfRemoved(CallbackInfo ci) {
        if (Nerfs.isFallingBlockDupeRemoved() && ((FallingBlockEntity) (Object) this).isRemoved())
            ci.cancel();
    }

    /*@ModifyConstant(constant = @Constant(floatValue = 1.0f), method = "causeFallDamage")
    private float onFallDamageReduction(float constant) {
        return 3f;
    }*/
}
