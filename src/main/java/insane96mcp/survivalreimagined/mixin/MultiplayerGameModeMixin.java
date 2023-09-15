package insane96mcp.survivalreimagined.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiplayerGameModeMixin {

    @Shadow private int destroyDelay;

    @Shadow private GameType localPlayerMode;

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
    private void test(BlockPos pPosBlock, Direction pDirectionFacing, CallbackInfoReturnable<Boolean> cir) {
        if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(pPosBlock) && this.minecraft.player.getMainHandItem().getItem() instanceof DiggerItem)
            this.destroyDelay--;
    }
}
