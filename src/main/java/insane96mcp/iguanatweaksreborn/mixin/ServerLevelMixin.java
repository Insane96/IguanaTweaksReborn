package insane96mcp.iguanatweaksreborn.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.iguanatweaksreborn.module.world.weather.Weather;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z"))
    private boolean preventRainClear(boolean original) {
        return Tiredness.onSleepFinished((ServerLevel) (Object) this, original);
    }

    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "CONSTANT", args = "intValue=100000"))
    private int changeThunderChance(int original) {
        return Weather.getLightningStrikeChance((ServerLevel) (Object) this, original);
    }

}
