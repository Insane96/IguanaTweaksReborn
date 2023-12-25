package insane96mcp.iguanatweaksreborn.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
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

    @Shadow private BlockPos destroyBlockPos;

    @Shadow private ItemStack destroyingItem;

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
    private void fasterCrativeBlockBreaking(BlockPos pPosBlock, Direction pDirectionFacing, CallbackInfoReturnable<Boolean> cir) {
        if (this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(pPosBlock) && this.minecraft.player.getMainHandItem().getItem() instanceof DiggerItem)
            this.destroyDelay--;
    }

    //Fixes https://github.com/neoforged/NeoForge/issues/143
    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void fixHealingItemsResettingBreaking(BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemstack = this.minecraft.player.getMainHandItem();
        cir.setReturnValue(pPos.equals(this.destroyBlockPos) && !destroyingItem.shouldCauseBlockBreakReset(itemstack));
    }
}
