package insane96mcp.survivalreimagined.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.DoubleSupplier;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends Animal {
    protected AbstractHorseMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("RETURN"), method = "generateSpeed", cancellable = true)
    private static void generateSpeed(DoubleSupplier supplier, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((0.9d + supplier.getAsDouble() * 0.6d + supplier.getAsDouble() * 0.6d + supplier.getAsDouble() * 0.6d) * 0.25d);
    }
}
