package insane96mcp.iguanatweaksreborn.mixin.client;

import insane96mcp.iguanatweaksreborn.event.ITEventFactory;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(at = @At("RETURN"), method = "hasEnoughFoodToStartSprinting", cancellable = true)
    private void canSprint(CallbackInfoReturnable<Boolean> callback) {
        if (!ITEventFactory.doPlayerSprintCheck((LocalPlayer) (Object) this))
            callback.setReturnValue(false);
    }
}
