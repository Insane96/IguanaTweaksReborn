package insane96mcp.iguanatweaksreborn.mixin.integration.sereneseasons;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import sereneseasons.handler.season.RandomUpdateHandler;

@Mixin(RandomUpdateHandler.class)
public class RandomUpdateHandlerMixin {
    @ModifyArg(method = "meltInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private static BlockPos onGetBiome(BlockPos blockPos, @Local(ordinal = 1) BlockPos topGroundPos) {
        return topGroundPos;
    }
}
