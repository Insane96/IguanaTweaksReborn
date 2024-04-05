package insane96mcp.iguanatweaksreborn.mixin.integration.autumnity;

import com.teamabnormals.autumnity.common.block.TurkeyBlock;
import com.teamabnormals.autumnity.core.registry.AutumnityItems;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.NoHunger;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurkeyBlock.class)
public class TurkeyBlockMixin {
    @Inject(method = "restoreHunger", at = @At("HEAD"), remap = false)
    public void onEat(LevelAccessor worldIn, Player player, CallbackInfo ci) {
        if (Feature.isEnabled(NoHunger.class))
            NoHunger.healOnEat(player, AutumnityItems.TURKEY_PIECE.get(), AutumnityItems.AutumnityFoods.TURKEY);
    }
}
