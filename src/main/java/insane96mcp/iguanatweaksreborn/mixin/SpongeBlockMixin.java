package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.misc.tweaks.Tweaks;
import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {
    @ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 65))
    public int onDrainLimit(int limit) {
        return Tweaks.changeMaxSpongeSoakBlocks(limit);
    }

    @ModifyConstant(method = "removeWaterBreadthFirstSearch", constant = @Constant(intValue = 6))
    public int onCrawlRange(int range) {
        return Tweaks.changeSpongeMaxRange(range);
    }
}
