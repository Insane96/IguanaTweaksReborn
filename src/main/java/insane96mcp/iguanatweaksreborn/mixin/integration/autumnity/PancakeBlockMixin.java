package insane96mcp.iguanatweaksreborn.mixin.integration.autumnity;

import com.teamabnormals.autumnity.common.block.PancakeBlock;
import com.teamabnormals.autumnity.core.registry.AutumnityBlocks;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.NoHunger;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.integration.AutumnityIntegration;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PancakeBlock.class)
public class PancakeBlockMixin {
    @Inject(method = "eatCake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"), remap = false)
    public void onEat(Level worldIn, BlockPos pos, BlockState state, Player player, ItemStack itemstack, CallbackInfoReturnable<InteractionResult> cir) {
        if (Feature.isEnabled(NoHunger.class))
            NoHunger.healOnEat(player, AutumnityBlocks.PANCAKE.get().asItem(), AutumnityIntegration.FOOD_PROPERTIES);
    }
}
