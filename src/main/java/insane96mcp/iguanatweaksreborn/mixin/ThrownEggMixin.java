package insane96mcp.iguanatweaksreborn.mixin;

import insane96mcp.iguanatweaksreborn.module.farming.livestock.Livestock;
import insane96mcp.insanelib.base.Feature;
import net.minecraft.world.entity.projectile.ThrownEgg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrownEgg.class)
public class ThrownEggMixin {
    @ModifyConstant(method = "onHit", constant = @Constant(intValue = 8))
    public int chanceForChicken(int chance) {
        return !Feature.isEnabled(Livestock.class) ? chance : Livestock.chickenFromEggChance;
    }
}
