package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.farming.crops.Crops;
import net.minecraft.world.level.block.FarmBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {
    @ModifyConstant(method = "isNearWater", constant = {@Constant(intValue = 4), @Constant(intValue = -4)})
    private static int onWaterHydrationRadius(int radius) {
        return radius > 0 ? Crops.getWaterHydrationRadius() : -Crops.getWaterHydrationRadius();
    }
}
