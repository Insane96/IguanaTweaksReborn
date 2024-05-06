package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @ModifyExpressionValue(method = "hurt", at = @At(value = "CONSTANT", args = "floatValue=40.0"))
    public float damageToBreak(float damageToBreak) {
        return 20f;
    }
}
