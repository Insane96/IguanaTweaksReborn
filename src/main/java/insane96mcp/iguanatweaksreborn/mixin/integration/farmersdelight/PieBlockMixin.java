package insane96mcp.iguanatweaksreborn.mixin.integration.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.NoHunger;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.PieBlock;

@Mixin(PieBlock.class)
public class PieBlockMixin {
    @Inject(method = "consumeBite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    public void onConsumeBite(Level level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<InteractionResult> cir, @Local FoodProperties foodProperties, @Local ItemStack sliceStack) {
        if (Feature.isEnabled(NoHunger.class))
            NoHunger.healOnEat(player, sliceStack.getItem(), foodProperties);
    }
}
